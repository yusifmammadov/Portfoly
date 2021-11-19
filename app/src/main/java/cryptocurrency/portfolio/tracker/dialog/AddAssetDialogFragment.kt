package cryptocurrency.portfolio.tracker.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import cryptocurrency.portfolio.tracker.adapters.AddAssetApapter
import cryptocurrency.portfolio.tracker.databinding.LayoutAddAssetDialogBinding
import cryptocurrency.portfolio.tracker.db.Asset
import cryptocurrency.portfolio.tracker.model.AssetData
import cryptocurrency.portfolio.tracker.portfolio.PortfolioViewModel

class AddAssetDialogFragment: DialogFragment() {

    private var width: Int = 0
    private var height: Int = 0

    private var assetSymbol: String? = null
    private var assetIconUrl: String? = null
    private var assetMarketId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = LayoutAddAssetDialogBinding.inflate(layoutInflater)
        val portfolioViewModel: PortfolioViewModel by viewModels(ownerProducer = {requireParentFragment()})

        val adapter = AddAssetApapter(context!!)
        binding.selectAssetDropDownMenu.setAdapter(adapter)
        binding.selectAssetDropDownMenu.setOnItemClickListener { adapterView, view, position, l ->

            binding.inputPriceEditText.requestFocus()
//            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
            setAssetData(adapter.getItem(position))
        }

        portfolioViewModel.getAllAssetData().observe(viewLifecycleOwner) {
            adapter.data = it
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.addButton.setOnClickListener {
            val price = binding.inputPriceEditText.text.toString()
            val amount = binding.inputAmountEditText.text.toString()
            if (binding.selectAssetDropDownMenu.text.isNotBlank() && price!!.isNotBlank()
                && amount!!.isNotBlank()) {
                val asset = Asset (
                    assetSymbol, price.toDouble(), amount.toDouble(), assetIconUrl, assetMarketId
                        )
                portfolioViewModel.addAssettoPortfolio(asset)
            }
            dismiss()
        }


        width  = (resources.displayMetrics.widthPixels*0.94).toInt()
        height = (width*0.92).toInt()

        dialog?.window?.setLayout(width, height)

        return binding.root
    }

    private fun setAssetData(item: AssetData?) {
        assetSymbol = item?.symbol
        assetIconUrl = item?.iconUrl
        assetMarketId = item?.marketId
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(width, height) ?: Log.v("CreatePortfolioDialog", "dialog is null oncreate")
    }
}