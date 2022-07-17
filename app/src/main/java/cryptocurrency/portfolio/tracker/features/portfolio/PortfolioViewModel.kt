package cryptocurrency.portfolio.tracker.features.portfolio

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import cryptocurrency.portfolio.tracker.data.PortfolioRepository
import cryptocurrency.portfolio.tracker.data.db.entities.Asset
import cryptocurrency.portfolio.tracker.data.db.entities.Portfolio
import cryptocurrency.portfolio.tracker.util.Constants
import cryptocurrency.portfolio.tracker.util.PortfolioEvent
import cryptocurrency.portfolio.tracker.util.prepareMarketData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(private val repository: PortfolioRepository,
                                             private val savedStateHandle: SavedStateHandle,
                                             private val app: Application):
    ViewModel() {


    init {
        getPortfolios()
    }

    private var lastJob: Job? = null

    private val _portfolioStateUi = MutableLiveData<PortfolioStateUi>(PortfolioStateUi())
    val portfolioStateUi: LiveData<PortfolioStateUi>
    get() = _portfolioStateUi


    private val portfolioEventChannel = Channel<PortfolioEvent>()
    val portfolioEvent = portfolioEventChannel.receiveAsFlow()

    fun getPortfolios() = viewModelScope.launch {

            repository.getPortfolioList().collect { portfolios ->
                if (!portfolios.isEmpty()) {

                    val portfolioIdFromSavedState = savedStateHandle.get<Int>("portfolioId")
                    val isIdNull = portfolioIdFromSavedState == null || portfolioIdFromSavedState == -1
                    _portfolioStateUi.postValue(_portfolioStateUi
                        .value?.copy(
                            portfolioList = portfolios,
                            currPortfolioId = if (isIdNull)  portfolios.last().id!! else portfolioIdFromSavedState
                        )
                    )
                    setCurrPortfolio(if (isIdNull) portfolios.last().id!! else portfolioIdFromSavedState!!)
                } else {
                    _portfolioStateUi.postValue(_portfolioStateUi
                        .value?.copy(
                            hasPortfolio = false
                        )
                    )
                }

            }
    }

    fun getAllAssetData() = repository.getAllCoins()

    fun addAssettoPortfolio(asset: Asset) {


        viewModelScope.launch {
            try {

            val id = repository.addAsset(asset.apply {
                this.portfolioId = _portfolioStateUi.value?.currPortfolioId
            })
            updateAssetMarketData(asset, id.toInt())

            } catch (e: Exception) { }
        }

    }

    private suspend fun updateAssetMarketData(asset: Asset, assetId: Int) {
        val priceResponse = repository.getAssetPrice(asset.marketId)

        asset.prepareMarketData(priceResponse[asset.marketId]?.get("usd")?.asDouble)
        asset.id = assetId
        repository.updateAsset(asset)
    }

    fun setCurrPortfolio(portfolioId: Int) {
        lastJob?.cancel()
        lastJob = CoroutineScope(Dispatchers.Main).launch {
            repository.getAssetList(portfolioId).collect { resource ->
                _portfolioStateUi.postValue(
                    _portfolioStateUi.value?.copy(
                        assetList = resource,
                        currPortfolioId = portfolioId
                    )
                )
            }
        }
    }

    fun onAssetSwiped(asset: Asset?) = asset?.let {
            viewModelScope.launch {
                repository.deleteAsset(it)
                portfolioEventChannel.send(PortfolioEvent.ShowUndoDeleteAssetMessage(it))
            }
        }

    fun undoDeleteAsset(asset: Asset) = viewModelScope.launch {
        repository.addAsset(asset)
    }

    fun onDeletePortfolioClicked() = _portfolioStateUi.value?.currPortfolioId?.let {
        viewModelScope.launch {
            repository.deletePortfolio(it)
        }
    }

    fun createPortfolio(title: String) = viewModelScope.launch {
        repository.addPortfolio(Portfolio(
            null, title
        ))
    }

    fun setHasPortfolio() {
        app.getSharedPreferences(Constants.PORTFOLY_SHARED_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(Constants.HAS_PORTFOLIO, false)
            .apply()
    }

    override fun onCleared() {
        super.onCleared()
        lastJob?.cancel()
    }
}