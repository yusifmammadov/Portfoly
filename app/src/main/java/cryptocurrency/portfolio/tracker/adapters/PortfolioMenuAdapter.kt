package cryptocurrency.portfolio.tracker.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.data.db.entities.Portfolio

class PortfolioMenuAdapter(contextWelcome: Context):
    ArrayAdapter<Portfolio>(contextWelcome, 0) {

    var data: List<Portfolio> = emptyList()
    set(value) {
        clear()
        addAll(value)
        field = value
        Log.d("PortfolioMenuAdapter", "data: $value")
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val itemView: View

        if (convertView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.portfolio_dropdown_item, parent, false)
        } else {
            itemView = convertView
        }

        val textView: TextView = itemView.findViewById(R.id.titleDropdownItem)
        textView.text = data[position].title
        return itemView
    }

    override fun getItem(position: Int): Portfolio? {

        Log.d("PortfolioMenuAdapter", "count is $count")
        return data[position]
    }

//    override fun getItemId(position: Int): Long  = data[position].id!!.toLong()

    fun getItemWithId(id: Int): Portfolio {
        return data.filter { portfolio ->
            portfolio.id == id
        }.first()
    }


}