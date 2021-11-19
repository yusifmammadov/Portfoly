package cryptocurrency.portfolio.tracker.db

import androidx.lifecycle.LiveData
import androidx.room.*
import cryptocurrency.portfolio.tracker.model.AssetData

@Dao
interface PortfolioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(portfolio: Portfolio): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllAssetData(listAssetData: List<AssetData>)

    @Query("SELECT * FROM portfolios")
    fun getAllPortfolios(): LiveData<List<Portfolio>>

    @Query("SELECT * FROM portfolios WHERE id = :id")
    fun getPortfolioWithId(id: Int): LiveData<Portfolio>

    @Query("SELECT * FROM asset_listing")
    fun getAllAssetData(): LiveData<List<AssetData>>

    @Query("UPDATE portfolios SET assets = :listAssets WHERE id = :portfolioId")
    suspend fun updatePortfolioAssetList(portfolioId: Int, listAssets: List<Asset>)

    @Update
    suspend fun updatePortfolio(portfolio: Portfolio)
}