package cryptocurrency.portfolio.tracker.portfolio

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import cryptocurrency.portfolio.tracker.PortfolioRepository
import cryptocurrency.portfolio.tracker.db.entities.Asset
import cryptocurrency.portfolio.tracker.db.entities.Portfolio
import cryptocurrency.portfolio.tracker.util.PortfolioEvent
import cryptocurrency.portfolio.tracker.util.Resource
import cryptocurrency.portfolio.tracker.util.prepareMarketData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

private const val LOG_TAG = "PortfolioViewModel"

@HiltViewModel
class PortfolioViewModel @Inject constructor(private val repository: PortfolioRepository,
    private val savedStateHandle: SavedStateHandle,
    private val app: Application):
    ViewModel() {

    private val _currPortfolio = MutableLiveData<Int>()
    val currentPortfolio: LiveData<Int>
    get() = _currPortfolio

    val assetsList: LiveData<Resource<List<Asset>>> = _currPortfolio.switchMap { id ->
        repository.getAssetList(id).asLiveData()
    }

    private val _portfolioAddedOrDeleted = MutableLiveData<Boolean>(false)
    val portfolioAddedOrDeleted: LiveData<Boolean>
    get() = _portfolioAddedOrDeleted





    private val portfolioEventChannel = Channel<PortfolioEvent>()
    val portfolioEvent = portfolioEventChannel.receiveAsFlow()

    fun getAllAssetData() = repository.getAllCoins()

    fun addAssettoPortfolio(asset: Asset) {


        viewModelScope.launch {
            try {

            val id = repository.addAsset(asset.apply {
                this.portfolioId = _currPortfolio.value
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

    fun getListPortfolio() = repository.getPortfolioList()

    fun setCurrPortfolio(portfolioId: Int) {
        _currPortfolio.value = portfolioId
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

    fun onDeletePortfolioClicked() = _currPortfolio.value?.let {
        viewModelScope.launch {
            repository.deletePortfolio(it)
        }
    }

    fun createPortfolio(title: String) = viewModelScope.launch {
        repository.addPortfolio(Portfolio(
            null, title
        ))
    }

    fun setPortfolioAddedOrDeleted(b: Boolean) {
        _portfolioAddedOrDeleted.value = b
    }

    fun setHasPortfolio() {
        app.getSharedPreferences("Portfolio", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("hasPortfolio", false)
            .apply()
    }
}