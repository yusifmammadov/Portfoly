package cryptocurrency.portfolio.tracker.portfolio

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import cryptocurrency.portfolio.tracker.AssetItemTouchHelper
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.adapters.AssetListAdapter
import cryptocurrency.portfolio.tracker.adapters.PortfolioMenuAdapter
import cryptocurrency.portfolio.tracker.callbacks.DeleteItemCallback
import cryptocurrency.portfolio.tracker.databinding.FragmentPortfolioBinding
import cryptocurrency.portfolio.tracker.dialog.AddAssetDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val LOG_TAG = "PortfolioFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [PortfolioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PortfolioFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var viewModel: PortfolioViewModel

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

        binding = FragmentPortfolioBinding.inflate(layoutInflater)

        val viewModelFactory = PortfolioViewModelFactory(requireActivity().application)
        val portfolioViewModel by viewModels<PortfolioViewModel>(ownerProducer = {this}){ viewModelFactory }
        viewModel = portfolioViewModel

        val menuAdapter = PortfolioMenuAdapter(context!!)
        val listAdapter = AssetListAdapter()

        binding.assetList.adapter = listAdapter

        val itemTouchHelper = ItemTouchHelper(AssetItemTouchHelper(object : DeleteItemCallback {
            override fun deleteItem(position: Int) {
                viewModel.deleteAsset(position)
                listAdapter.notifyItemRemoved(position)
            }
        })).attachToRecyclerView(binding.assetList)

        binding.portfolioDropdownMenu.setAdapter(menuAdapter)

        binding.portfolioDropdownMenu.setOnItemClickListener { adapterView, view, position, l ->

            val id = menuAdapter.getItem(position)?.id
            getAssetList(id)
        }

        binding.fab.setOnClickListener {
            AddAssetDialogFragment().show(childFragmentManager, "addAssetDailog")
        }


        viewModel.portfolioList.observe(viewLifecycleOwner) {

            Log.v(LOG_TAG, "portfolioList: $it")
            it?.let {

                viewModel.setDropdownMenuItems()
//                viewModel.setCurrentPortfolio(it[0].id)
//                viewModel.getAssetListForId(it[0].id)
            }
        }

        viewModel.listPortfolioItems.observe(viewLifecycleOwner) {
            it?.let {
                menuAdapter.data = it

                val position = it.size - 1
                val title = menuAdapter.getItem(position)?.name
                val id = menuAdapter.getItem(position)?.id
                binding.portfolioDropdownMenu.setText(title, false)
                getAssetList(id)
            }
        }

        viewModel.listAssetItems.observe(viewLifecycleOwner) {
            it?.let {
                listAdapter.data = it
            }
        }



        return binding.root
    }

    private fun setEmptyPortfolioViews() {
        binding.addAssetText.visibility = View.VISIBLE
        binding.addAssetButtonEmpty.visibility = View.VISIBLE
        binding.assetList.visibility = View.GONE

        binding.addAssetButtonEmpty.setOnClickListener {
            AddAssetDialogFragment().show(childFragmentManager, "addAssetDailog")
        }
    }

    private fun getAssetList(id: Int?) {
        viewModel.getAssetListForId(id)?.let {
            it.observe(viewLifecycleOwner) {
                viewModel.currPortfolio = it
                if (it.assets == null) {
                    setEmptyPortfolioViews()
                } else {
                    viewModel.prepareAssetItems(it.assets!!)
                    Log.v(LOG_TAG, "assets: ${it.assets}")

                }

            }
        }
    }
}