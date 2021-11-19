package cryptocurrency.portfolio.tracker.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portfolios")
data class Portfolio(
    val title: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    var assets: List<Asset?>? = null
}
