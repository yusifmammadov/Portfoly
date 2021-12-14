package cryptocurrency.portfolio.tracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cryptocurrency.portfolio.tracker.db.entities.Asset
import cryptocurrency.portfolio.tracker.db.entities.Portfolio
import cryptocurrency.portfolio.tracker.db.entities.AssetData
import cryptocurrency.portfolio.tracker.db.entities.LastUpdated

@Database(
    entities = [Portfolio::class, Asset::class, AssetData::class, LastUpdated::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class PortfolioDatabase: RoomDatabase() {

    abstract fun getPortfolioDao(): PortfolioDao

}