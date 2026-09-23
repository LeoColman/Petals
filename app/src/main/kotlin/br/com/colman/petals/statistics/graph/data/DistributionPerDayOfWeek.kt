package br.com.colman.petals.statistics.graph.data

import androidx.compose.ui.unit.dp
import br.com.colman.petals.statistics.graph.color.createColor
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalGrams
import io.grafima.charts.line.LineDataPoint
import io.grafima.charts.line.LineSeries
import java.time.DayOfWeek

private val StrokeWidth = 6.dp

private fun calculateGramDistributionPerDayOfWeek(uses: List<Use>): List<LineDataPoint> {
  val daysOfWeek = DayOfWeek.entries
  val usesPerDayOfWeek = daysOfWeek.associateWith { uses.filter { u -> u.date.dayOfWeek == it } }
  return usesPerDayOfWeek.mapValues { it.value.totalGrams }.toSortedMap()
    .map { (day, grams) -> LineDataPoint(day.value.toFloat(), grams.toFloat()) }
}

/**
 * One period's grams by weekday. Unfilled, unlike the per-hour chart: several periods overlap here
 * and stacked washes of colour hide the lines they belong to.
 */
fun createDistributionPerDayOfWeekSeries(days: Int, uses: List<Use>, label: String): LineSeries {
  return LineSeries(
    id = "weekday-$days",
    label = label,
    points = calculateGramDistributionPerDayOfWeek(uses),
    color = createColor(days),
    strokeWidth = StrokeWidth
  )
}
