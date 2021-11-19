package cryptocurrency.portfolio.tracker.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import coil.load
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.databinding.LayoutAddAssetDialogBinding
import cryptocurrency.portfolio.tracker.db.Asset
import cryptocurrency.portfolio.tracker.db.PortfolioItem
import cryptocurrency.portfolio.tracker.model.AssetData

private const val LOG_TAG = "AddAssetAdapter"
class AddAssetApapter(mContext: Context): ArrayAdapter<AssetData>(mContext, 0) {

    var data = listOf<AssetData>()
        set(value) {
            field = value
            addAll(value)
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView: View
        if (convertView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.add_asset_dropdown_item, parent, false)
        } else {
            itemView = convertView
        }
        val item = getItem(position)
        val imgView: ImageView = itemView.findViewById(R.id.assetIconImageView)
        imgView.load(item?.iconUrl)
        val textView: TextView = itemView.findViewById(R.id.addAssetSymbolTextView)
        textView.text = item?.symbol

        return itemView
    }

//    override fun getCount(): Int  = data.size
//
//    override fun getItem(position: Int): AssetData? = data[position]

    override fun getFilter(): Filter = filter

    private val filter = object : Filter() {
        override fun performFiltering(filterTerm: CharSequence?): FilterResults {
            val suggestions = mutableListOf<AssetData>()
            val results = FilterResults()

            if (filterTerm == null || filterTerm.isEmpty()) {
                suggestions.addAll(data)
            } else {
                val filterPattern = filterTerm.toString().uppercase().trim()
                Log.v(LOG_TAG, "filter pattern: $filterPattern")
                suggestions.addAll(data.filter {
                    it.symbol.contains(filterPattern)
                })
            }
            results.values = suggestions
            results.count = suggestions.size
            Log.v(LOG_TAG, "result values: $suggestions")
            return results
        }

        @Suppress("unchecked_cast")
        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            clear()
            Log.v(LOG_TAG, "results to publish: ${p1?.values}")
            addAll(p1?.values as List<AssetData>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as AssetData).symbol
        }
    }
}