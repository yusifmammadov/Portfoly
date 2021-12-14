package cryptocurrency.portfolio.tracker.util

import android.graphics.Color
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import cryptocurrency.portfolio.tracker.db.entities.Asset
import cryptocurrency.portfolio.tracker.model.MarketData

val colors: List<Int> = ColorTemplate.MATERIAL_COLORS.toMutableList() + ColorTemplate.VORDIPLOM_COLORS.toMutableList()

fun Asset.prepareMarketData(price: Double?) {
    price?.let { price ->

        val amount = this.amount / this.avgPrice
        val priceChange = (price - this.avgPrice)/this.avgPrice*100
        val changeUsd = this.amount * priceChange / 100
        val amountUsd = amount * price
        this.marketData = MarketData(amount, amountUsd, priceChange, changeUsd)
    }
}

fun List<Asset>.createDataForPieChart(): PieData {
    val entries = mutableListOf<PieEntry>()

    this.forEach { asset ->
        asset.marketData?.let {
            entries.add(PieEntry(it.usdAmount?.toFloat() ?: 0.0f, asset.symbol))
        }
    }

    val pieDataSet = PieDataSet(entries, "Portfolio")
    pieDataSet.setColors(colors)
    pieDataSet.valueFormatter = PercentageFormatter()
    pieDataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
    pieDataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
    pieDataSet.valueTextColor = Color.WHITE
    pieDataSet.valueLineColor = Color.WHITE
    return PieData(pieDataSet)

}
