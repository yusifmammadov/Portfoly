package cryptocurrency.portfolio.tracker.features.portfolio

import cryptocurrency.portfolio.tracker.data.db.entities.Asset
import cryptocurrency.portfolio.tracker.data.db.entities.Portfolio
import cryptocurrency.portfolio.tracker.util.Resource

data class PortfolioStateUi(
    val portfolioList: List<Portfolio>? = null,
    val assetList: Resource<List<Asset>>? = null,
    val currPortfolioId: Int? = null,
    val hasPortfolio:Boolean = true
)
