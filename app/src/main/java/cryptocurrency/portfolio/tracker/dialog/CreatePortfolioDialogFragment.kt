package cryptocurrency.portfolio.tracker.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import cryptocurrency.portfolio.tracker.databinding.LayoutPortfolioDialogBinding
import java.lang.ClassCastException
import java.lang.IllegalStateException

class CreatePortfolioDialogFragment() : DialogFragment() {

    internal lateinit var listener: CreatePortfolioDialogListener

    interface CreatePortfolioDialogListener {
        fun onCreatePortfolio(title: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            listener = parentFragment as CreatePortfolioDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement dialog listener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            val binding = LayoutPortfolioDialogBinding.inflate(inflater)


            binding.button2.setOnClickListener {
                listener.onCreatePortfolio(binding.portfolioTitleInput.text.toString())
                dismiss()
            }
            AlertDialog.Builder(it)
                .setView(binding.root)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}