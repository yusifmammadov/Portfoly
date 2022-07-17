package cryptocurrency.portfolio.tracker.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.features.portfolio.PortfolioViewModel

class DeletePortfolioDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

       val portfolioViewModel: PortfolioViewModel by viewModels(ownerProducer = {requireParentFragment()})

       return MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_Portfoly_MaterialAlertDialog)
            .setMessage(getString(R.string.delete_portfolio))
            .setPositiveButton(getString(R.string.delete), object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    portfolioViewModel.onDeletePortfolioClicked()

                    Toast.makeText(context, getString(R.string.portfolio_deleted_toast), Toast.LENGTH_SHORT).show()
                }
            })
            .setNegativeButton(getString(R.string.cancel), object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    dismiss()
                }
            })
            .create()
    }
}