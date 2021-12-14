package cryptocurrency.portfolio.tracker.db;

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cryptocurrency.portfolio.tracker.db.entities.Asset
import cryptocurrency.portfolio.tracker.model.MarketData

class Converters {


    @TypeConverter
    fun fromMarketData(marketData: MarketData?): String? {
        marketData?.let {
            val json = Gson().toJson(it)
            return json.toString()
        } ?: return null
    }

    @TypeConverter
    fun toMarketData(s: String?): MarketData? {
        s?.let {
            return Gson().fromJson<MarketData>(it, MarketData::class.java)
        } ?: return null
    }
}
