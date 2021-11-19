package cryptocurrency.portfolio.tracker.welcome

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cryptocurrency.portfolio.tracker.callbacks.CreatePortfolioDialogCallback
import cryptocurrency.portfolio.tracker.databinding.FragmentWelcomeBinding
import cryptocurrency.portfolio.tracker.dialog.CreatePortfolioDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val LOG_TAG = "WelcomeFragment"
/**
 * A simple [Fragment] subclass.
 * Use the [WelcomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WelcomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding = FragmentWelcomeBinding.inflate(layoutInflater)

        val viewModelFactory = WelcomeViewModelFactory(requireActivity().application)
        val viewModel by viewModels<WelcomeViewModel> { viewModelFactory }

        binding.button.setOnClickListener {
            CreatePortfolioDialogFragment(object : CreatePortfolioDialogCallback {
                override fun onPortfolioCreated(title: String) {
                    viewModel.createNewPortfolio(title)
                    Log.v(LOG_TAG, "portfolio name: $title")
                    findNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToPortfolioFragment())
                }
            }).show(requireActivity().supportFragmentManager, "createPortfolioDialog")
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WelcomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WelcomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}