package cryptocurrency.portfolio.tracker.portfolio

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import cryptocurrency.portfolio.tracker.api.MarketApi
import cryptocurrency.portfolio.tracker.api.MarketApiService
import cryptocurrency.portfolio.tracker.db.*
import cryptocurrency.portfolio.tracker.model.AssetItem
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.text.DecimalFormat


private const val LOG_TAG = "PortfolioViewModel"
class PortfolioViewModel(app: Application): AndroidViewModel(app) {

    val portfolioList: LiveData<List<Portfolio>>
    val portfolioDao: PortfolioDao
    val db: PortfolioDatabase
    val marketApiService: MarketApiService
    var currPortfolio: Portfolio? = null


    init {
        db = PortfolioDatabase.invoke(getApplication<Application>().applicationContext)
        portfolioDao = db.getPortfolioDao()
        portfolioList = portfolioDao.getAllPortfolios()
        marketApiService = MarketApi.retrofitService
    }

    private val _listPortfolioItems = MutableLiveData<List<PortfolioItem>>()
    val listPortfolioItems: LiveData<List<PortfolioItem>>
    get() = _listPortfolioItems

    private val _listAssetItems = MutableLiveData<List<AssetItem>>()
    val listAssetItems: LiveData<List<AssetItem>>
    get() = _listAssetItems


    fun setDropdownMenuItems() {
        val portfolioItemList = mutableListOf<PortfolioItem>()

        portfolioList.value?.forEach {
            portfolioItemList.add(
                PortfolioItem(
                    it.id!!,
                    it.title
                )
            )
        }
        _listPortfolioItems.value = portfolioItemList
    }

    fun getAssetListForId(id: Int?): LiveData<Portfolio>? {
        return id?.let {
            portfolioDao.getPortfolioWithId(it)
        }
    }

    fun getAllAssetData() = portfolioDao.getAllAssetData()

    fun addAssettoPortfolio(asset: Asset) {

        viewModelScope.launch {
            try {
                val assetList = currPortfolio?.assets?.toMutableList() ?: mutableListOf()
                assetList.add(asset)
                portfolioDao.updatePortfolioAssetList(currPortfolio?.id!!, assetList.toList() as List<Asset>)

            } catch (e: Exception) {
                Log.v(LOG_TAG, "could not add asset: $e")
            }
        }

    }

    fun prepareAssetItems(assets: List<Asset?>) {
        var ids = ""
        assets.forEach {
            ids = ids + it?.marketId + ","
        }

        viewModelScope.launch {
            try {
                val response = marketApiService.getPrice(ids, "usd").body()
                Log.v(LOG_TAG, "$response")
                response?.let {priceResponse ->
                    val listAssetItems = assets.map { asset ->
                        val assetPrice = priceResponse[asset?.marketId]?.get("usd")?.asDouble
                        val priceChange = ((assetPrice!! - asset!!.avgPrice!!)/asset.avgPrice!!*100)
                        val changeUsd = asset.amount!!*priceChange/100
                        val amount = asset.amount!!/asset.avgPrice!!
                        val amountUsd = amount*assetPrice
                        Log.v(LOG_TAG, "assetPrice: ${assetPrice}, priceChange: $priceChange," +
                                " changeUsd: $changeUsd, amount: $amount, amountUsd: $amountUsd")
                        AssetItem(asset.symbol!!, amount, amountUsd,
                        priceChange, changeUsd, asset.logoUrl!!)
                    }

                    _listAssetItems.postValue(listAssetItems)
                }
                Log.v(LOG_TAG, "response success: $response")
            } catch (e: Exception) {
                Log.v(LOG_TAG, "response error: ${e.message}")
            }
        }
    }

    fun deleteAsset(position: Int) {
        val assetList = currPortfolio?.assets?.toMutableList()
        assetList?.removeAt(position)
        currPortfolio?.assets = assetList
        viewModelScope.launch {
            //portfolioDao.updatePortfolioAssetList(currPortfolio!!.id!!, assetList?.toList() as List<Asset>)
            portfolioDao.updatePortfolio(currPortfolio!!)
        }

    }

}

class PortfolioViewModelFactory(val application: Application): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PortfolioViewModel::class.java)) {
            return PortfolioViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}