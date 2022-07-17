package cryptocurrency.portfolio.tracker.data

import androidx.room.withTransaction
import cryptocurrency.portfolio.tracker.data.api.MarketApiService
import cryptocurrency.portfolio.tracker.data.db.PortfolioDatabase
import cryptocurrency.portfolio.tracker.data.db.entities.Asset
import cryptocurrency.portfolio.tracker.data.db.entities.AssetData
import cryptocurrency.portfolio.tracker.data.db.entities.Portfolio
import cryptocurrency.portfolio.tracker.data.api.model.MarketData
import cryptocurrency.portfolio.tracker.data.api.model.PriceResponse
import cryptocurrency.portfolio.tracker.util.UpdateResult
import cryptocurrency.portfolio.tracker.util.networkBoundResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class PortfolioRepository @Inject constructor(
    private val api: MarketApiService,
    private val db: PortfolioDatabase
) {

    private val portfolioDao = db.getPortfolioDao()

    private var listAssets: List<Asset>? = null

    fun getAssetList(id: Int) = networkBoundResource(
        query = {
            portfolioDao.getAssetListForId(id)
        },
        fetch = {
            var ids = ""
            listAssets = portfolioDao.getAssets(id)
            listAssets?.forEach {
                ids = ids + it.marketId + ","
            }
             api.getPrice(ids.dropLast(1))
        },

        saveFetchResult = { priceResponse ->
            portfolioDao.updateAssets(listAssets!!.prepare(priceResponse))
        },

        shouldFetch = { listAssets ->
            if (listAssets.isNotEmpty()) {
                val lastUpdated = listAssets.lastOrNull()?.lastUpdated

                val shouldFetch =  lastUpdated == null ||
                        (System.currentTimeMillis() - lastUpdated) > TimeUnit.MINUTES.toMillis(10)
                shouldFetch
            } else false
        }
    )

    private fun List<Asset>.prepare( pr: PriceResponse): List<Asset> {
        return this.map { asset ->
            val assetPrice = pr[asset.marketId]?.get("usd")?.asDouble
            var changeUsd: Double? = null
            var priceChange: Double? = null
            var amountUsd: Double? = null
            val amount = asset.amount / asset.avgPrice
            assetPrice?.let {
                priceChange = (it - asset.avgPrice)/asset.avgPrice*100
                changeUsd = asset.amount * priceChange!! / 100
                amountUsd = amount * assetPrice
            }

            Asset(asset.id, asset.symbol, asset.avgPrice, asset.amount,
            asset.logoUrl, asset.marketId, asset.portfolioId,
            MarketData(
                amount, amountUsd, priceChange, changeUsd)
            )
        }
    }


    suspend fun addPortfolio(portfolio: Portfolio) = portfolioDao.addPortfolio(portfolio)

    fun getPortfolioList() = portfolioDao.getAllPortfolios()

    fun getAllCoins() = portfolioDao.getAllAssetData()

    suspend fun addAsset(asset: Asset) = portfolioDao.upsertAsset(asset)

    suspend fun getAssetPrice(ids: String) = api.getPrice(ids)

    suspend fun updateAsset(asset: Asset) = portfolioDao.updateAsset(asset)

    suspend fun deleteAsset(asset: Asset) = portfolioDao.deleteAsset(asset)

    suspend fun deletePortfolio(id: Int) {
        portfolioDao.deletePortfolio(id)
        portfolioDao.deleteAssetsOfPortfolio(id)
    }

    suspend fun updateAssetListing(): UpdateResult {

        var longArray = LongArray(0)
        getAllAssetsFromApi()?.let {
            if (!it.isNullOrEmpty()) {
                db.withTransaction {
                    portfolioDao.deleteAllAssets()
                    longArray = portfolioDao.addAllAssets(it)
                }

            }
        }
        return if (!longArray.isEmpty()) {
             UpdateResult.Success()
        } else UpdateResult.Failure()
    }

    suspend fun getAllAssetsFromApi(): List<AssetData>? {

        var page = 0
        var isResponseNullOrEmpty = false
        val listAssetData = mutableListOf<AssetData>()

        while (!isResponseNullOrEmpty) {
            val response = api.getAllAssets(pageNum = page)

            if (response.isSuccessful) {
                val body = response.body()
                if (!body.isNullOrEmpty()) {
                    body.forEach {
                        listAssetData.add(
                            AssetData(
                                null,
                                it.symbol.uppercase(),
                                it.image,
                                it.id
                            )
                        )
                    }
                    page++
                } else {
                    isResponseNullOrEmpty = true
                }
            } else {
                return null
            }
            delay(1000)
        }
        return listAssetData
    }


}