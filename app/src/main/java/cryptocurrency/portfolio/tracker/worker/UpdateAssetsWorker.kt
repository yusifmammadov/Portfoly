package cryptocurrency.portfolio.tracker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cryptocurrency.portfolio.tracker.PortfolioRepository
import cryptocurrency.portfolio.tracker.util.UpdateResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.lang.Exception


@HiltWorker
class UpdateAssetsWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val repository: PortfolioRepository
    ) : CoroutineWorker(ctx, params) {


    override suspend fun doWork(): Result {

        return try {
            val result = repository.updateAssetListing()
            if (result is UpdateResult.Success) {
                Result.success()
            } else Result.failure()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val UPDATE_ASSETS_WORKER = "cryptocurrency.portfolio.tracker.worker.UpdateAssets"
    }
}