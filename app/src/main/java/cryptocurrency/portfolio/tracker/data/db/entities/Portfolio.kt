package cryptocurrency.portfolio.tracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portfolios")
data class Portfolio(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val title: String
) {
    override fun toString(): String = title
}
