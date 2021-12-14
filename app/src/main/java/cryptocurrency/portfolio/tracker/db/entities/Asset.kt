package cryptocurrency.portfolio.tracker.db

data class Asset(
    val symbol: String?,
    var avgPrice: Double?,
    var amount: Double?,
    val logoUrl: String?,
    val marketId: String?,
)

