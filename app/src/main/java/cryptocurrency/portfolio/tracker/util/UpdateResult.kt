package cryptocurrency.portfolio.tracker.util

sealed class UpdateResult {

    class Success: UpdateResult()
    class Failure: UpdateResult()
}