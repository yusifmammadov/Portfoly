package cryptocurrency.portfolio.tracker.util

import cryptocurrency.portfolio.tracker.data.db.entities.Asset

sealed class PortfolioEvent {

    data class ShowUndoDeleteAssetMessage(val asset:Asset): PortfolioEvent()
}
