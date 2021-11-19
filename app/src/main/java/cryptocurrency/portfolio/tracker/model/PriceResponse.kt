package cryptocurrency.portfolio.tracker.model

import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal

import kotlin.collections.HashMap

val typeToken = object : TypeToken<HashMap<String, AssetPrice>>() {}.type

class PriceResponse: HashMap<String, JsonObject>()

data class AssetPrice(
    val price: Double
)

data class some(
    val usd: Double
)
data class Something(
    val binanomics: Binanomics,
    val binapet: Binapet,
    val bitcoin: Bitcoin,
    val goldmoney: Goldmoney
)

data class Binanomics(
    val usd: Double
)

data class Binapet(
    val usd: Double
)

data class Bitcoin(
    val usd: Double
)

data class Goldmoney(
    val usd: Double
)