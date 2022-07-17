package cryptocurrency.portfolio.tracker.data.db;

import androidx.room.TypeConverter
import com.google.gson.Gson
import cryptocurrency.portfolio.tracker.data.api.model.MarketData

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
