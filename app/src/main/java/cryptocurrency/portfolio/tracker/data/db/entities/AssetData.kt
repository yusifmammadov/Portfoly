package cryptocurrency.portfolio.tracker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "asset_listing")
data class AssetData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val symbol: String,
    val iconUrl: String,
    val marketId: String
){
    override fun toString(): String = symbol.uppercase()
}
