package cryptocurrency.portfolio.tracker.welcome

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cryptocurrency.portfolio.tracker.db.Portfolio
import cryptocurrency.portfolio.tracker.db.PortfolioDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val LOG_TAG = "WelcomeViewModel"

class WelcomeViewModel(val app: Application): AndroidViewModel(app) {

    val roomDb = PortfolioDatabase.invoke(getApplication<Application>().applicationContext)

    val portfolioDao = roomDb.getPortfolioDao()

    var newPortfolioTitle: String? = null

    val viewModelJob = Job()
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    fun createNewPortfolio(title: String) {

        coroutineScope.launch {

            try {
                portfolioDao.upsert(Portfolio(
                    title
                )).also {
                    Log.v(LOG_TAG, "id: $it")
                }
            } catch (e: Exception) {
                Log.v(LOG_TAG, "exception message: ${e.message}")
            }

        }

        val sharedPrefs = getApplication<Application>().getSharedPreferences("Portfolio", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("hasPortfolio", true).apply()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

class WelcomeViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}