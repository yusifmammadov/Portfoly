package cryptocurrency.portfolio.tracker.util

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class PercentageFormatter : ValueFormatter() {

    private val decFormat = DecimalFormat("###,###,##")

    override fun getFormattedValue(value: Float): String = decFormat.format(value) + "%"
}