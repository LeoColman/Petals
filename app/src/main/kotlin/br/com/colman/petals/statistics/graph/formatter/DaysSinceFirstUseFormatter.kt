package br.com.colman.petals.statistics.graph.formatter

import br.com.colman.petals.use.repository.Use
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DaysSinceFirstUseFormatter(uses: List<Use>, dateFormat: String) {

  val formatDate = object : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
      val formatter = DateTimeFormatter.ofPattern(dateFormat)
      val dayBeforeFirstUseDateToEpochDay: Long = uses.minBy { it.date }.localDate.toEpochDay().dec()

      val epochDay = (value + dayBeforeFirstUseDateToEpochDay).toLong()
      val localDate = LocalDate.ofEpochDay(epochDay)

      return formatter.format(localDate)
    }
  }
}
