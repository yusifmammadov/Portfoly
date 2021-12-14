package cryptocurrency.portfolio.tracker.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cryptocurrency.portfolio.tracker.databinding.FragmentWelcomeBinding
import cryptocurrency.portfolio.tracker.dialog.CreatePortfolioDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class WelcomeFragment : Fragment(), CreatePortfolioDialogFragment.CreatePortfolioDialogListener {

    private lateinit var viewModel: WelcomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding = FragmentWelcomeBinding.inflate(layoutInflater)

        val welcomeViewModel: WelcomeViewModel by viewModels()
        viewModel = welcomeViewModel

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
        return binding.root
    }

    override fun onCreatePortfolio(title: String) {
        viewModel.createNewPortfolio(title)
    }
}