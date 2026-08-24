package br.com.colman.petals.statistics.graph.data

import br.com.colman.petals.statistics.graph.color.createColor
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalGrams
import io.grafima.charts.line.LineDataPoint
import io.grafima.charts.line.LineSeries

private const val FillAlpha = 0.3f

private fun calculateGramDistributionPerHour(uses: List<Use>): List<LineDataPoint> {
  val hoursInDay = (0..23)
  val usesPerHourOfDay = hoursInDay.associateWith { uses.filter { a -> a.date.hour == it } }
  return usesPerHourOfDay.mapValues { it.value.totalGrams }
    .toSortedMap().map { (hour, grams) -> LineDataPoint(hour.toFloat(), grams.toFloat()) }
}

/**
 * One period's grams by hour of day. Points carry no label of their own, which leaves the hour
 * labels to the axis formatter rather than to the twenty-four points underneath it.
 */
fun createDistributionPerHourSeries(days: Int, uses: List<Use>, label: String): LineSeries {
  val color = createColor(days)
  return LineSeries(
    id = "hour-$days",
    label = label,
    points = calculateGramDistributionPerHour(uses),
    color = color,
    fillAlpha = FillAlpha
  )
}
