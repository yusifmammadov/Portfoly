package cryptocurrency.portfolio.tracker.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.adapters.AddAssetApapter
import cryptocurrency.portfolio.tracker.databinding.LayoutAddAssetDialogBinding
import cryptocurrency.portfolio.tracker.data.db.entities.Asset
import cryptocurrency.portfolio.tracker.data.db.entities.AssetData
import cryptocurrency.portfolio.tracker.features.portfolio.PortfolioViewModel
import cryptocurrency.portfolio.tracker.util.Constants

class AddAssetDialogFragment: DialogFragment() {

    private var assetSymbol: String? = null
    private var assetIconUrl: String? = null
    private var assetMarketId: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        try{
            val inflater = requireActivity().layoutInflater
            val binding = LayoutAddAssetDialogBinding.inflate(inflater)

            val portfolioViewModel: PortfolioViewModel by viewModels(ownerProducer = {requireParentFragment()})

            val adapter = AddAssetApapter(requireContext())
            binding.selectAssetDropDownMenu.setAdapter(adapter)
            binding.selectAssetDropDownMenu.setOnItemClickListener { adapterView, view, position, l ->

                binding.inputPriceEditText.requestFocus()
                setAssetData(adapter.getItem(position))
            }

            binding.selectAssetDropDownMenu.doOnTextChanged { text, start, before, count ->
                setAssetDataToNull()
            }

            portfolioViewModel.getAllAssetData().observe(this) {
                adapter.data = it
                savedInstanceState?.let { saved ->
                    val symbol = saved.getString(Constants.ASSET_SYMBOL)
                    val url = saved.getString(Constants.ASSET_ICON_URL)
                    val marketId = saved.getString(Constants.ASSET_MARKET_ID)

                    if (symbol != null && url != null && marketId != null) {
                        setAssetData(
                            AssetData(
                                null, symbol, url, marketId
                            )
                        )
                    }
                }
            }


            binding.cancelButton.setOnClickListener {
                dismiss()
            }
            binding.addButton.setOnClickListener {
                val price = binding.inputPriceEditText.text.toString()
                val amount = binding.inputAmountEditText.text.toString()
                val symbol = binding.selectAssetDropDownMenu.text.toString()
                if (symbol.isNotBlank() && price.isNotBlank()
                    && amount.isNotBlank()) {
                        if (adapter.containsAssetData(symbol)) {
                            if (assetMarketId != null) {
                                val asset = Asset (null, assetSymbol, price.toDouble(),
                                    amount.toDouble(), assetIconUrl, assetMarketId!!, null, null)
                                portfolioViewModel.addAssettoPortfolio(asset)
                                dismiss()
                            } else {
                                Toast.makeText(context, getString(R.string.select_asset_toast), Toast.LENGTH_SHORT)
                                    .show()
                            }

                        } else {
                            Toast.makeText(context, getString(R.string.asset_not_found), Toast.LENGTH_SHORT)
                                .show()
                        }

                } else {
                    Toast.makeText(context, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT)
                        .show()
                }

            }

            return AlertDialog.Builder(requireActivity())
                .setView(binding.root)
                .create()
        } catch (e: Exception) {
            return AlertDialog.Builder(requireActivity())
                .setMessage("${e.message}")
                .create()
        }
    }

    private fun setAssetData(item: AssetData?) {
        assetSymbol = item?.symbol
        assetIconUrl = item?.iconUrl
        assetMarketId = item?.marketId
    }

    private fun setAssetDataToNull() {
        assetSymbol = null
        assetIconUrl = null
        assetMarketId = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.ASSET_SYMBOL, assetSymbol)
        outState.putString(Constants.ASSET_ICON_URL, assetIconUrl)
        outState.putString(Constants.ASSET_MARKET_ID, assetMarketId)
    }


}