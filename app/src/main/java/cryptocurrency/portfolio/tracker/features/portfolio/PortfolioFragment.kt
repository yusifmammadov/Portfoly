package cryptocurrency.portfolio.tracker.features.portfolio

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.google.android.material.snackbar.Snackbar
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.adapters.AssetAdapter
import cryptocurrency.portfolio.tracker.adapters.PortfolioMenuAdapter
import cryptocurrency.portfolio.tracker.databinding.FragmentPortfolioBinding
import cryptocurrency.portfolio.tracker.data.db.entities.Asset
import cryptocurrency.portfolio.tracker.dialog.AddAssetDialogFragment
import cryptocurrency.portfolio.tracker.dialog.CreatePortfolioDialogFragment
import cryptocurrency.portfolio.tracker.dialog.DeletePortfolioDialogFragment
import cryptocurrency.portfolio.tracker.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class PortfolioFragment : Fragment(), CreatePortfolioDialogFragment.CreatePortfolioDialogListener {

    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var viewModel: PortfolioViewModel
    private lateinit var listAdapter: AssetAdapter
    private var shouldAnimate = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPortfolioBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PortfolioViewModel::class.java)
        shouldAnimate = true
        val portfolioMenuAdapter = PortfolioMenuAdapter(requireContext())

        listAdapter = AssetAdapter()
        binding.assetList.adapter = listAdapter

        val trashIcon: Drawable = getDrawable(requireContext(), R.drawable.ic_baseline_delete_36)!!

        val itemTouchHelper = ItemTouchHelper(object : PortfolioItemTouchHelperCallback(trashIcon) {
            override fun onAssetSwiped(pos: Int) {
                viewModel.onAssetSwiped(listAdapter.currentList[pos])
            }
        }).attachToRecyclerView(binding.assetList)

        with(binding.portfolioDropdownMenuToolbar) {
            setAdapter(portfolioMenuAdapter)

            setOnItemClickListener { adapterView, view, position, l ->

            val portfolio = portfolioMenuAdapter.getItem(position)
            shouldAnimate = true
            viewModel.setCurrPortfolio(portfolio?.id!!)
            }
        }

        setToolbar()

        binding.fab.setOnClickListener {
            AddAssetDialogFragment().show(childFragmentManager, "addAssetDailog")
        }

        viewModel.portfolioStateUi.observe(viewLifecycleOwner) { portfolioState ->

            if (portfolioState.hasPortfolio) {
                portfolioState.portfolioList?.let {

                    portfolioMenuAdapter.data = it

                    val currPortfolio = portfolioMenuAdapter.getItemWithId(portfolioState.currPortfolioId!!)

                    binding.portfolioDropdownMenuToolbar.setText(currPortfolio.title, false)

                    when(portfolioState.assetList) {
                        is Resource.Loading -> {
                            if (portfolioState.assetList.data != null) {
                                Toast.makeText(context, getString(R.string.refresh), Toast.LENGTH_SHORT).show()
                            }
                            updateUi(portfolioState.assetList)
                        }
                        is Resource.Success -> updateUi(portfolioState.assetList)
                        is Resource.Error -> {
                            updateUi(portfolioState.assetList)
                            Toast.makeText(context, getString(R.string.could_not_refresh), Toast.LENGTH_SHORT)
                        }
                        else -> {}
                    }
                }
            } else {
                try {
                    viewModel.setHasPortfolio()
                    findNavController().navigate(PortfolioFragmentDirections.actionPortfolioFragmentToWelcomeFragment())
                } catch (e: Exception) {}
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.portfolioEvent.collect { portfolioEvent ->
                when(portfolioEvent){
                    is PortfolioEvent.ShowUndoDeleteAssetMessage ->
                        Snackbar.make(requireView(), getString(R.string.asset_deleted), Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.undo)) {
                                viewModel.undoDeleteAsset(portfolioEvent.asset)
                            }.show()
                }
            }
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        val id = viewModel.portfolioStateUi.value?.currPortfolioId ?: -1
        outState.putInt(Constants.PORTFOLIO_ID, id)
        super.onSaveInstanceState(outState)
    }

    override fun onCreatePortfolio(title: String) {
        viewModel.createPortfolio(title)
    }


    private fun updateUi(resource: Resource<List<Asset>>) {


            if (resource.data.isNullOrEmpty()) {
                setEmptyPortfolioViews()
            } else {
                binding.addAssetText.visibility = View.GONE
                binding.addAssetButtonEmpty.visibility = View.GONE
                binding.assetList.visibility = View.VISIBLE

                if (shouldAnimate) {
                    val animationController = AnimationUtils.loadLayoutAnimation(
                        context,
                        R.anim.layout_animation
                    )
                    binding.assetList.layoutAnimation = animationController
                    binding.pieChart.animateY(1000, Easing.EaseInOutQuad)
                    shouldAnimate = false
                }
                listAdapter.submitList(resource.data)
                binding.pieChart.description.isEnabled = false
                binding.pieChart.setDrawEntryLabels(false)
                binding.pieChart.setUsePercentValues(true)
                binding.pieChart.setHoleColor(R.color.background)
                binding.pieChart.legend.textColor = Color.WHITE
                binding.pieChart.legend.form = Legend.LegendForm.CIRCLE
                binding.pieChart.legend.isWordWrapEnabled = true

                binding.pieChart.data = resource.data.createDataForPieChart()
                binding.pieChart.invalidate()
            }

    }

    private fun setEmptyPortfolioViews() {
        val addAssetText = binding.addAssetText
        val addButton = binding.addAssetButtonEmpty
        addAssetText.visibility = View.VISIBLE
        addButton.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        addAssetText.animation = animation
        addButton.animation = animation
        addAssetText.animate()
        addButton.animate()
        binding.assetList.visibility = View.GONE
        binding.pieChart.clear()
        binding.addAssetButtonEmpty.setOnClickListener {
            AddAssetDialogFragment().show(childFragmentManager, "addAssetDailog")
        }
    }

    private fun setToolbar() {
        with(binding.toolbar) {
            inflateMenu(R.menu.toolbar_menu)

            setOnMenuItemClickListener { menuItem ->

                when(menuItem.itemId) {
                    R.id.add -> {
                        CreatePortfolioDialogFragment()
                            .show(childFragmentManager, "createPortfolioDialog")
                        true
                    }
                    R.id.delete -> {

                        DeletePortfolioDialogFragment().show(childFragmentManager,
                            "deletePortfolioDialog")
                        true
                    }
                    else -> {
                        true
                    }
                }

            }
        }
    }
}