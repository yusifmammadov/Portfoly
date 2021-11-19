package cryptocurrency.portfolio.tracker.model

data class AssetItem(
        val symbol: String,
        val amount: Double,
        val amountUsd: Double,
        val changePerc: Double,
        val changeUsd: Double,
        val iconUrl: String
)
