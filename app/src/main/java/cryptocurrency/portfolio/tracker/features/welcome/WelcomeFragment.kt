package cryptocurrency.portfolio.tracker.features.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cryptocurrency.portfolio.tracker.databinding.FragmentWelcomeBinding
import cryptocurrency.portfolio.tracker.dialog.CreatePortfolioDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class WelcomeFragment : Fragment(), CreatePortfolioDialogFragment.CreatePortfolioDialogListener {

    private lateinit var viewModel: WelcomeViewModel
    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(WelcomeViewModel::class.java)

        binding.button.setOnClickListener {
            CreatePortfolioDialogFragment().show(childFragmentManager, "createPortfolioDialog")
        }

        viewModel.navigateToPortfolioFragment.observe(viewLifecycleOwner) {
            if (it) {
                try {
                    findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToPortfolioFragment())
                } catch (e: Exception) {}
            }
        }
    }

    override fun onCreatePortfolio(title: String) {
        viewModel.createNewPortfolio(title)
    }
}