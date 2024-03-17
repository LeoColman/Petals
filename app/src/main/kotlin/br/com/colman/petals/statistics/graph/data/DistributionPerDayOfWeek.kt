package br.com.colman.petals.statistics.graph.data

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.toArgb
import br.com.colman.petals.statistics.graph.color.createColor
import br.com.colman.petals.statistics.graph.formatter.GramsValueFormatter
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalGrams
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import java.time.DayOfWeek

private fun calculateGramDistributionPerDayOfWeek(uses: List<Use>): List<Entry> {
  val daysOfWeek = DayOfWeek.entries
  val usesPerDayOfWeek = daysOfWeek.associateWith { uses.filter { u -> u.date.dayOfWeek == it } }
  return usesPerDayOfWeek.mapValues { it.value.totalGrams }.toSortedMap()
    .map { (k, v) -> Entry(k.value.toFloat(), v.toFloat()) }
}

fun createDistributionPerDayOfWeekDataset(days: Int, uses: List<Use>, label: String, colors: Colors): LineDataSet {
  return LineDataSet(calculateGramDistributionPerDayOfWeek(uses), label).apply {
    valueFormatter = GramsValueFormatter
    lineWidth = 6f
    setDrawCircles(true)
    setDrawFilled(false)
    setDrawValues(true)
    fillColor = createColor(days).toArgb()
    color = createColor(days).toArgb()
    valueTextColor = colors.secondary.toArgb()
    valueTextSize = 14f
  }
}
