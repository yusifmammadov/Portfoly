package cryptocurrency.portfolio.tracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import cryptocurrency.portfolio.tracker.api.MarketApi
import cryptocurrency.portfolio.tracker.api.MarketApiService
import cryptocurrency.portfolio.tracker.db.PortfolioDao
import cryptocurrency.portfolio.tracker.db.PortfolioDatabase
import cryptocurrency.portfolio.tracker.model.AssetData
import cryptocurrency.portfolio.tracker.model.MarketResponse
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import java.lang.IllegalArgumentException

private const val LOG_TAG = "MainActivityViewModel"
class MainActivityViewModel(app: Application): AndroidViewModel(app) {

    val portfolioDao: PortfolioDao
    val db: PortfolioDatabase

    init {
        db = PortfolioDatabase.invoke(getApplication<Application>().applicationContext)
        portfolioDao = db.getPortfolioDao()
    }

    private val marketApiService: MarketApiService = MarketApi.retrofitService
    private var listAllAssets = mutableListOf<AssetData>()
    private var assetsPage: Int = 1

    private val _listAllCryptoAssets = MutableLiveData<List<AssetData>>()
    val listAllCryptoAssets: LiveData<List<AssetData>>
    get() = _listAllCryptoAssets

    private val _isLoadingAllAssets = MutableLiveData<Boolean>(true)
    val isLoadingAllAssets: LiveData<Boolean>
    get() = _isLoadingAllAssets


    fun getAssetListFromApi() {

        viewModelScope.launch {
            try {
                val response = marketApiService.getAllAssets("usd", assetsPage, 250)

                handleMarketResponse(response)
            } catch (e: Exception) {
                Log.v(LOG_TAG, "error message: ${e.message}")
            }

        }
    }

    private suspend fun handleMarketResponse(response: Response<MarketResponse>) {

        if (response.isSuccessful) {
            response.body()?.let {
                if(!it.isEmpty()) {
                    val list = mutableListOf<AssetData>()

                    response.body()?.forEach {
                        list.add(
                            AssetData(
                                null,
                                it.symbol.uppercase(),
                                it.image,
                                it.id
                            )
                        )
                    }
                    listAllAssets.addAll(list)
                    assetsPage++
                    getAssetListFromApi()
                } else  {
                    _listAllCryptoAssets.postValue(listAllAssets)
                    portfolioDao.upsertAllAssetData(listAllAssets)
                }
            }
        }
    }

    fun loadingFinished() {
        _isLoadingAllAssets.value = false
    }
}

class MainActivityViewModelFactory(val app: Application): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}