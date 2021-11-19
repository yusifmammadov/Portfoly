package cryptocurrency.portfolio.tracker.db

data class PortfolioItem(
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}
