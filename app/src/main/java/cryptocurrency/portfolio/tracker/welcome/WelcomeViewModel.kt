package cryptocurrency.portfolio.tracker.welcome

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import cryptocurrency.portfolio.tracker.PortfolioRepository
import cryptocurrency.portfolio.tracker.db.entities.Portfolio
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
            val sharedPrefs = app.getSharedPreferences("Portfolio", Context.MODE_PRIVATE)
            sharedPrefs.edit().putBoolean("hasPortfolio", true).apply()
            _navigateToPortfolioFragment.value = true
        }
    }
}