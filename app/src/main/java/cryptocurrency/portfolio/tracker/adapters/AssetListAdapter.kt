package cryptocurrency.portfolio.tracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.databinding.PortfolioListItemBinding
import cryptocurrency.portfolio.tracker.db.Asset
import cryptocurrency.portfolio.tracker.model.AssetItem
import java.text.DecimalFormat

val decUsd = DecimalFormat("0.00")
val decAmount = DecimalFormat("0.00000")

class AssetListAdapter: RecyclerView.Adapter<AssetListAdapter.ViewHolder>() {

    var data = listOf<AssetItem>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount(): Int = data.size

    fun swipedDone(holder: ViewHolder) {
        holder.binding.swipeDelete.visibility = View.VISIBLE
        holder.binding.item.visibility = View.GONE
    }


    class ViewHolder private constructor(val binding: PortfolioListItemBinding): RecyclerView.ViewHolder(binding.root) {


        fun bind(assetItem: AssetItem) {
            binding.assetIcon.load(assetItem.iconUrl)
            binding.assetSymbol.text = assetItem.symbol
            binding.assetAmountBase.text = decAmount.format(assetItem.amount)
            val amountUsd = "$" + decUsd.format(assetItem.amountUsd)
            binding.assetAmountUsd.text = amountUsd
            val changeUsd = "$" + decUsd.format(assetItem.changeUsd)
            binding.assetChangeAmount.text = changeUsd
            binding.assetChangeAmount.setChangeColor(assetItem.changeUsd)
            val changePerc = decUsd.format(assetItem.changePerc) + "%"
            binding.assetChangePercentage.text = changePerc
            binding.assetChangePercentage.setChangeColor(assetItem.changePerc)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = PortfolioListItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }

         private fun TextView.setChangeColor(change: Double) {
             if (change < 0) {
                 this.setTextColor(resources.getColor(R.color.redFall, null))
             } else this.setTextColor(resources.getColor(R.color.greenRise, null))
        }
    }
}

