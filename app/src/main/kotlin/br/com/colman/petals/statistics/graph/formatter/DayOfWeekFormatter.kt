package br.com.colman.petals.statistics.graph.formatter

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val DayOfWeekFormatter = object : IAxisValueFormatter {
  private val formatter = DateTimeFormatter.ofPattern("E")

  override fun getFormattedValue(value: Float, axis: AxisBase?): String {
    val locale = Locale.getDefault()
    val dayOfWeek = DayOfWeek.of(value.toInt())
    val dateWithDayOfWeek = LocalDate.now().with(dayOfWeek)

    return dateWithDayOfWeek.format(formatter.withLocale(locale)).replace(".", "")
  }
}
