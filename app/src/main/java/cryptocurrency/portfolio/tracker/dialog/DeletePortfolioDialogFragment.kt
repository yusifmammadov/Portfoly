package cryptocurrency.portfolio.tracker.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.callbacks.DeleteItemCallback
import cryptocurrency.portfolio.tracker.db.entities.Portfolio
import cryptocurrency.portfolio.tracker.portfolio.PortfolioViewModel

class DeletePortfolioDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

       val portfolioViewModel: PortfolioViewModel by viewModels(ownerProducer = {requireParentFragment()})

       return MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_Portfoly_MaterialAlertDialog)
            .setMessage("Are you sure you want to delete portfolio?")
            .setPositiveButton("Delete", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    portfolioViewModel.setPortfolioAddedOrDeleted(true)
                    portfolioViewModel.onDeletePortfolioClicked()

                    Toast.makeText(context, "Portfolio Deleted ", Toast.LENGTH_SHORT).show()
                }
            })
            .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    dismiss()
                }
            })
            .create()
    }
}