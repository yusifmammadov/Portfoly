package cryptocurrency.portfolio.tracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "asset_listing")
data class AssetData(
    @PrimaryKey
    var id: Int? = null,
    val symbol: String,
    val iconUrl: String,
    val marketId: String
){
    override fun toString(): String = symbol.uppercase()
}
