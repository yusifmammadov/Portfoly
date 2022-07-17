package cryptocurrency.portfolio.tracker.data.db


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cryptocurrency.portfolio.tracker.data.db.entities.Asset
import cryptocurrency.portfolio.tracker.data.db.entities.Portfolio
import cryptocurrency.portfolio.tracker.data.db.entities.AssetData

@Database(
    entities = [Portfolio::class, Asset::class, AssetData::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class PortfolioDatabase: RoomDatabase() {

    abstract fun getPortfolioDao(): PortfolioDao

}