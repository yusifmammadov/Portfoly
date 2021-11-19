package cryptocurrency.portfolio.tracker.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import cryptocurrency.portfolio.tracker.callbacks.CreatePortfolioDialogCallback
import cryptocurrency.portfolio.tracker.databinding.LayoutPortfolioDialogBinding

class CreatePortfolioDialogFragment(val callback: CreatePortfolioDialogCallback) : DialogFragment() {

    private var width: Int = 0
    private var height: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = LayoutPortfolioDialogBinding.inflate(layoutInflater)

        width  = (resources.displayMetrics.widthPixels*0.94).toInt()
        height = (width*0.92).toInt()

        binding.button2.setOnClickListener {
            callback.onPortfolioCreated(binding.portfolioTitleInput.text.toString())
            dismiss()
        }



        dialog?.window?.setLayout(width, height) ?: Log.v("CreatePortfolioDialog", "dialog is null oncreate")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(width, height) ?: Log.v("CreatePortfolioDialog", "dialog is null onresume")
    }
}