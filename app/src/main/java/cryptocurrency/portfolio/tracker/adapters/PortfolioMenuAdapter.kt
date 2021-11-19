package cryptocurrency.portfolio.tracker.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.db.PortfolioItem

class PortfolioMenuAdapter(contextWelcome: Context):
    ArrayAdapter<PortfolioItem>(contextWelcome, 0) {

   var data = listOf<PortfolioItem>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun getCount(): Int = data.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val itemView: View

        if (convertView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.portfolio_dropdown_item, parent, false)
        } else {
            itemView = convertView
        }

        val textView: TextView = itemView.findViewById(R.id.titleDropdownItem)
        textView.text = getItem(position)?.name
        Log.v("ArrayAdapter", "text set: ${getItem(position)?.name}")
        return itemView
    }



    override fun getItem(position: Int): PortfolioItem? = data[position]


}