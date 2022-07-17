package cryptocurrency.portfolio.tracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import cryptocurrency.portfolio.tracker.data.db.entities.Asset
import cryptocurrency.portfolio.tracker.data.db.entities.Portfolio
import cryptocurrency.portfolio.tracker.data.db.entities.AssetData
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPortfolio(portfolio: Portfolio): Long

    @Query("SELECT * FROM portfolios")
    fun getAllPortfolios(): Flow<List<Portfolio>>

    @Update
    suspend fun updateAssets(listAssets: List<Asset>)

    @Delete
    suspend fun deleteAsset(asset: Asset)

    @Insert
    suspend fun upsertAsset(asset: Asset): Long

    @Update
    suspend fun updateAsset(asset: Asset)

    @Query("SELECT * FROM assets WHERE portfolioId = :id")
    fun getAssetListForId(id: Int): Flow<List<Asset>>

    @Query("SELECT * FROM assets WHERE portfolioId = :id")
    suspend fun getAssets(id: Int): List<Asset>

    @Query("DELETE FROM portfolios WHERE id = :id")
    suspend fun deletePortfolio(id: Int)

    @Query("DELETE FROM assets WHERE portfolioId = :portfolioId")
    suspend fun deleteAssetsOfPortfolio(portfolioId: Int)


    @Query("SELECT * FROM asset_listing")
    fun getAllAssetData(): LiveData<List<AssetData>>

    @Query("DELETE FROM asset_listing")
    suspend fun deleteAllAssets()

    @Insert
    suspend fun addAllAssets(data: List<AssetData>): LongArray

}