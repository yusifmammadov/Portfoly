package cryptocurrency.portfolio.tracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.databinding.PortfolioListItemBinding
import cryptocurrency.portfolio.tracker.db.entities.Asset
import java.text.DecimalFormat

val decUsd = DecimalFormat("0.00")
val decAmount = DecimalFormat("0.00000")

class AssetAdapter: ListAdapter<Asset, AssetAdapter.ViewHolder>(AssetDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class ViewHolder private constructor(val binding: PortfolioListItemBinding): RecyclerView.ViewHolder(binding.root) {


        fun bind(asset: Asset) {

            val marketData = asset.marketData
            Glide.with(binding.assetIcon.context)
                .load(asset.logoUrl)
                .placeholder(R.drawable.ic_coin_svgrepo_com)
                .into(binding.assetIcon)

            binding.assetSymbol.text = asset.symbol
            binding.assetAmountBase.text = marketData?.originalAmount?.let {
                decAmount.format(it)
            } ?: "N/A"
            val amountUsd = marketData?.usdAmount?.let {
                "$" + decUsd.format(it)
            } ?: "N/A"
            binding.assetAmountUsd.text = amountUsd
            val changeUsd = marketData?.changeUsd?.let {
                "$" + decUsd.format(it)
            } ?: "N/A"
            binding.assetChangeAmount.text = changeUsd
            binding.assetChangeAmount.setChangeColor(marketData?.changeUsd ?: 0.0)
            val changePerc = marketData?.changePerc?.let {
                decUsd.format(it) + "%"
            } ?: "N/A"
            binding.assetChangePercentage.text = changePerc
            binding.assetChangePercentage.setChangeColor(marketData?.changePerc ?: 0.0)
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
             } else if (change > 0) {
                 this.setTextColor(resources.getColor(R.color.greenRise, null))
             } else this.setTextColor(resources.getColor(R.color.primaryTextColor, null))

        }
    }

    class AssetDiff : DiffUtil.ItemCallback<Asset>() {

        override fun areItemsTheSame(oldItem: Asset, newItem: Asset): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Asset, newItem: Asset): Boolean =
            oldItem == newItem
    }
}

