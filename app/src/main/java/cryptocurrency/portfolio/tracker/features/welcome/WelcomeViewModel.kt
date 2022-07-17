package cryptocurrency.portfolio.tracker.features.welcome

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import cryptocurrency.portfolio.tracker.data.PortfolioRepository
import cryptocurrency.portfolio.tracker.data.db.entities.Portfolio
import cryptocurrency.portfolio.tracker.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class WelcomeViewModel @Inject constructor(private val repository: PortfolioRepository,
                                           private val app: Application): ViewModel() {

    private val _navigateToPortfolioFragment = MutableLiveData<Boolean>(false)
    val navigateToPortfolioFragment: LiveData<Boolean>
    get() = _navigateToPortfolioFragment

    fun createNewPortfolio(title: String) {

        viewModelScope.launch {

            repository.addPortfolio(
                Portfolio(null, title))
            val sharedPrefs = app.getSharedPreferences(Constants.PORTFOLY_SHARED_PREFS, Context.MODE_PRIVATE)
            sharedPrefs.edit().putBoolean(Constants.HAS_PORTFOLIO, true).apply()
            _navigateToPortfolioFragment.value = true
        }
    }
}