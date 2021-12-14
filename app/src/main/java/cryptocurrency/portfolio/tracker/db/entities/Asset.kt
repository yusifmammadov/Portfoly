package cryptocurrency.portfolio.tracker.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cryptocurrency.portfolio.tracker.model.MarketData

@Entity(tableName = "assets")
data class Asset(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val symbol: String?,
    var avgPrice: Double,
    var amount: Double,
    val logoUrl: String?,
    val marketId: String,
    var portfolioId: Int?,
    var marketData: MarketData?,
    val lastUpdated: Long = System.currentTimeMillis()
)

