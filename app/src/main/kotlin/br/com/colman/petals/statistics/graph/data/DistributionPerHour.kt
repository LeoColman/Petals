package br.com.colman.petals.statistics.graph.data

import androidx.compose.ui.graphics.toArgb
import br.com.colman.petals.statistics.graph.color.createColor
import br.com.colman.petals.statistics.graph.formatter.GramsValueFormatter
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalGrams
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

private fun calculateGramDistributionPerHour(uses: List<Use>): List<Entry> {
  val hoursInDay = (0..23)
  val usesPerHourOfDay = hoursInDay.associateWith { uses.filter { a -> a.date.hour == it } }
  return usesPerHourOfDay.mapValues { it.value.totalGrams }
    .toSortedMap().map { (k, v) -> Entry(k.toFloat(), v.toFloat()) }
}

fun createDistributionPerHourDataset(days: Int, uses: List<Use>, label: String): LineDataSet {
  return LineDataSet(calculateGramDistributionPerHour(uses), label).apply {
    valueFormatter = GramsValueFormatter
    setDrawCircles(true)
    setDrawFilled(true)
    setDrawValues(false)
    fillColor = createColor(days).toArgb()
    color = createColor(days).toArgb()
    setCircleColor(createColor(days).toArgb())
  }
}
