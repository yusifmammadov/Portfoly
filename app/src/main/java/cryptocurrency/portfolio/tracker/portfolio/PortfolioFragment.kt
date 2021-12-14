package cryptocurrency.portfolio.tracker.portfolio

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.google.android.material.snackbar.Snackbar
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.adapters.AssetAdapter
import cryptocurrency.portfolio.tracker.adapters.PortfolioMenuAdapter
import cryptocurrency.portfolio.tracker.databinding.FragmentPortfolioBinding
import cryptocurrency.portfolio.tracker.db.entities.Asset
import cryptocurrency.portfolio.tracker.dialog.AddAssetDialogFragment
import cryptocurrency.portfolio.tracker.dialog.CreatePortfolioDialogFragment
import cryptocurrency.portfolio.tracker.dialog.DeletePortfolioDialogFragment
import cryptocurrency.portfolio.tracker.util.PortfolioEvent
import cryptocurrency.portfolio.tracker.util.Resource
import cryptocurrency.portfolio.tracker.util.createDataForPieChart
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class PortfolioFragment : Fragment(), CreatePortfolioDialogFragment.CreatePortfolioDialogListener {

    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var viewModel: PortfolioViewModel
    private lateinit var listAdapter: AssetAdapter
    private var portfolioAddedOrDeleted = false
    private var shouldAnimate = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPortfolioBinding.inflate(layoutInflater)

        val portfolioViewModel: PortfolioViewModel by viewModels(ownerProducer = {this})
        viewModel = portfolioViewModel

        val toolbar = binding.toolbar

        shouldAnimate = true

        val dropdownMenu = toolbar.findViewById<AutoCompleteTextView>(R.id.portfolioDropdownMenuToolbar)


        toolbar.inflateMenu(R.menu.toolbar_menu)

        toolbar.setOnMenuItemClickListener { menuItem ->

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

        //setToolbar(menuAdapter)

        listAdapter = AssetAdapter()
        binding.assetList.adapter = listAdapter



        val trashIcon: Drawable = getDrawable(requireContext(), R.drawable.ic_baseline_delete_36)!!
        val swipeBackground = ColorDrawable(Color.RED)

        val itemTouchHelper = ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.onAssetSwiped(listAdapter.currentList[viewHolder.bindingAdapterPosition])
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                if (dX < 0) {
                    val itemView = viewHolder.itemView
                    val iconMargin = (itemView.height - trashIcon.intrinsicHeight) / 2

                    trashIcon.setBounds(itemView.right - iconMargin - trashIcon.intrinsicWidth,
                        itemView.top + iconMargin, itemView.right - iconMargin,
                        itemView.top + trashIcon.intrinsicHeight + iconMargin)

                    swipeBackground.setBounds((itemView.right + dX - 20).toInt(), itemView.top,
                        itemView.right, itemView.bottom)

                    swipeBackground.draw(c)
                    trashIcon.draw(c)
                }
            }
        }).attachToRecyclerView(binding.assetList)


        binding.fab.setOnClickListener {
            AddAssetDialogFragment().show(childFragmentManager, "addAssetDailog")
        }

        viewModel.portfolioAddedOrDeleted.observe(viewLifecycleOwner) {
            portfolioAddedOrDeleted = it
        }

        viewModel.getListPortfolio().observe(viewLifecycleOwner) {

            it?.let { listPortfolio ->

                val menuAdapter = PortfolioMenuAdapter(context!!, listPortfolio)
                dropdownMenu.setAdapter(menuAdapter)
                dropdownMenu.setOnItemClickListener { adapterView, view, position, l ->

                    val portfolio = menuAdapter.getItem(position)
                    shouldAnimate = true
                    viewModel.setCurrPortfolio(portfolio?.id!!)
                }

                if (listPortfolio.lastIndex != -1) {


                    if (portfolioAddedOrDeleted) {
                        val currPortfolio = menuAdapter.getItem(listPortfolio.lastIndex)
                        dropdownMenu.setText(currPortfolio?.title, false)
                        shouldAnimate = true
                        viewModel.setCurrPortfolio(currPortfolio?.id!!)
                        viewModel.setPortfolioAddedOrDeleted(false)
                    } else if (savedInstanceState != null) {
                        val id = savedInstanceState.getInt("portfolioId")
                        if (id != -1) {
                            val currPortfolio = menuAdapter.getItemWithId(id)
                            dropdownMenu.setText(currPortfolio.title, false)
                            viewModel.setCurrPortfolio(id)
                        } else {
                            val currPortfolio = menuAdapter.getItem(listPortfolio.lastIndex)
                            dropdownMenu.setText(currPortfolio?.title, false)
                            viewModel.setCurrPortfolio(currPortfolio!!.id!!)
                        }
                    } else {
                        val currPortfolio = menuAdapter.getItem(listPortfolio.lastIndex)
                        dropdownMenu.setText(currPortfolio?.title, false)
                        viewModel.setCurrPortfolio(currPortfolio!!.id!!)
                    }

                } else {
                    try {
                        viewModel.setHasPortfolio()
                        findNavController().navigate(PortfolioFragmentDirections.actionPortfolioFragmentToWelcomeFragment())
                    } catch (e: Exception) {}

                }

            }
        }

        viewModel.assetsList.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    updateUi(resource)
                    if (resource.data != null) {
                        Toast.makeText(context, "Refreshing", Toast.LENGTH_SHORT).show()
                    }

                }
                is Resource.Success -> updateUi(resource)
                is Resource.Error -> {
                    updateUi(resource)
                    Toast.makeText(context, "Couldn't refresh", Toast.LENGTH_SHORT)
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.portfolioEvent.collect { portfolioEvent ->
                when(portfolioEvent){
                    is PortfolioEvent.ShowUndoDeleteAssetMessage ->
                        Snackbar.make(requireView(), "Asset deleted from portfolio", Snackbar.LENGTH_SHORT)
                            .setAction("UNDO") {
                                viewModel.undoDeleteAsset(portfolioEvent.asset)
                            }.show()
                }
            }
        }

        return binding.root
    }


    override fun onSaveInstanceState(outState: Bundle) {
        val id = viewModel.currentPortfolio.value ?: -1
        outState.putInt("portfolioId", id)
        super.onSaveInstanceState(outState)
    }

    override fun onCreatePortfolio(title: String) {
        viewModel.setPortfolioAddedOrDeleted(true)
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
}