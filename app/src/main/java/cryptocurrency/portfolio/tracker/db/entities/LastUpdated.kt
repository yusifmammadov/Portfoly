package cryptocurrency.portfolio.tracker.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LastUpdated(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val date: Long
)
