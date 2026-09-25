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
      val firstUseDay = uses.minBy { it.date }.localDate.toEpochDay()

      val epochDay = (value + firstUseDay).toLong()
      val localDate = LocalDate.ofEpochDay(epochDay)

      return formatter.format(localDate)
    }
  }
}
