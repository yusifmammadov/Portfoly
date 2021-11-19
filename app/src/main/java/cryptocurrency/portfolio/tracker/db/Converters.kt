package cryptocurrency.portfolio.tracker.db;

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromAsset(assets: List<Asset?>?): String? {
        assets?.let {
            val jsonAsset = Gson().toJson(assets)
            return jsonAsset.toString()
        } ?: return null

    }

    @TypeConverter
    fun toAsset(assetString: String?): List<Asset>? {
        assetString?.let {
            Log.v("roomtypeconverter", "string: $it")
            val listType = object: TypeToken<List<Asset>>() {}.type
            return Gson().fromJson<List<Asset>>(assetString, listType)
        } ?: return null

    }
}
