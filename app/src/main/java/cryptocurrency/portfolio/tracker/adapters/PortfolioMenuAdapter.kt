package cryptocurrency.portfolio.tracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import cryptocurrency.portfolio.tracker.R
import cryptocurrency.portfolio.tracker.db.entities.Portfolio

class PortfolioMenuAdapter(contextWelcome: Context, private val data: List<Portfolio>):
    ArrayAdapter<Portfolio>(contextWelcome, 0, data) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val itemView: View

        if (convertView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.portfolio_dropdown_item, parent, false)
        } else {
            itemView = convertView
        }

        val textView: TextView = itemView.findViewById(R.id.titleDropdownItem)
        textView.text = getItem(position)?.title
        return itemView
    }

    fun getItemWithId(id: Int): Portfolio {
        return data.filter { portfolio ->
            portfolio.id == id
        }.first()
    }


}