package cryptocurrency.portfolio.tracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.data.db.entities.AssetData


class AddAssetApapter(mContext: Context): ArrayAdapter<AssetData>(mContext, 0) {

    var data = listOf<AssetData>()
        set(value) {
            field = value
            clear()
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
        Glide.with(imgView.context)
            .load(item?.iconUrl)
            .placeholder(R.drawable.ic_coin_svgrepo_com)
            .into(imgView)
        val textView: TextView = itemView.findViewById(R.id.addAssetSymbolTextView)
        textView.text = item?.symbol

        return itemView
    }

    override fun getFilter(): Filter = filter

    private val filter = object : Filter() {
        override fun performFiltering(filterTerm: CharSequence?): FilterResults {
            val suggestions = mutableListOf<AssetData>()
            val results = FilterResults()

            if (filterTerm == null || filterTerm.isEmpty()) {
                suggestions.addAll(data)
            } else {
                val filterPattern = filterTerm.toString().uppercase().trim()
                suggestions.addAll(data.filter {
                    it.symbol.contains(filterPattern)
                })
            }
            results.values = suggestions
            results.count = suggestions.size
            return results
        }

        @Suppress("unchecked_cast")
        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            clear()
            addAll(p1?.values as List<AssetData>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as AssetData).symbol
        }
    }

   fun containsAssetData(s: String): Boolean {

       val asset = data.filter { assetData ->
           assetData.symbol.uppercase() == s.uppercase()
       }

       return asset.isNotEmpty()
   }
}