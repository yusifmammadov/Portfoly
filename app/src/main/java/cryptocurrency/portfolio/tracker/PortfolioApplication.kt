package cryptocurrency.portfolio.tracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.room.Update
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import cryptocurrency.portfolio.tracker.worker.UpdateAssetsWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class PortfolioApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        setupRecurringWork()
    }

    private fun setupRecurringWork()  = coroutineScope.launch {

        val updateAssetsRequest = PeriodicWorkRequestBuilder<UpdateAssetsWorker>(1,
            TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this@PortfolioApplication).enqueueUniquePeriodicWork(
            UpdateAssetsWorker.UPDATE_ASSETS_WORKER,
            ExistingPeriodicWorkPolicy.KEEP,
            updateAssetsRequest
        )
    }

}