package cryptocurrency.portfolio.tracker

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import cryptocurrency.portfolio.tracker.api.MarketApi
import cryptocurrency.portfolio.tracker.api.MarketApiService
import cryptocurrency.portfolio.tracker.db.PortfolioDao
import cryptocurrency.portfolio.tracker.db.PortfolioDatabase
import cryptocurrency.portfolio.tracker.db.entities.AssetData
import cryptocurrency.portfolio.tracker.model.MarketResponse
import cryptocurrency.portfolio.tracker.model.MarketResponseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import java.lang.IllegalArgumentException
import javax.inject.Inject

private const val LOG_TAG = "MainActivityViewModel"

@HiltViewModel
class MainActivityViewModel @Inject constructor(app: Application,  private val repository: PortfolioRepository):
    AndroidViewModel(app) {

    private var listAllAssets = mutableListOf<AssetData>()
    private var assetsPage: Int = 1


    fun getAssetListFromApi() {



        viewModelScope.launch {
            try {
                var isResponseNullOrEmpty = false

                while (!isResponseNullOrEmpty) {

                    val marketResponse = repository.getAllAssets(assetsPage)
                    Log.v(LOG_TAG, "getallassets called")
                    if (marketResponse.isSuccessful) {
                        val body = marketResponse.body()
                        if (!body.isNullOrEmpty())
                            isResponseNullOrEmpty = false
                            handleMarketResponse(body!!)
                    } else isResponseNullOrEmpty = true

                    assetsPage++
                }

                if(!listAllAssets.isEmpty()) {
                    repository.addAssetData(listAllAssets)
                    val sharedPrefs = getApplication<Application>().getSharedPreferences("Portfolio",
                        Context.MODE_PRIVATE)
                    sharedPrefs.edit().putBoolean("hasAssetList", true).apply()
                }
            } catch (e: Exception) {
                Log.v(LOG_TAG, "error message: ${e.message}")
            }

        }
    }

    private fun handleMarketResponse(response: MarketResponse) {

                if(!response.isEmpty()) {
                    val list = mutableListOf<AssetData>()

                    response.forEach {
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
                }
    }

}

//class MainActivityViewModelFactory(val app: Application): ViewModelProvider.Factory {
//
//    @Suppress("unchecked_cast")
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if(modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
//            return MainActivityViewModel(app) as T
//        }
//        throw IllegalArgumentException("Unknown viewmodel class")
//    }
//}