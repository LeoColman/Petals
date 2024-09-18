package br.com.colman.petals.statistics.graph.formatter

import br.com.colman.petals.koin
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
