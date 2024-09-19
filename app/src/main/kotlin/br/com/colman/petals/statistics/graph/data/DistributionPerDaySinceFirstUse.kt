package br.com.colman.petals.statistics.graph.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R
import br.com.colman.petals.statistics.graph.formatter.GramsValueFormatter
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalGrams
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalDate.now

@Composable
fun createAllTimeDistribution(uses: List<Use>): LineDataSet {
  val label = stringResource(R.string.grams_distribution_over_days_since_first_use)
  return createAllTimeLineDataSet(calculateAllTimeGramsDistribution(uses), label)
}

fun createAllTimeLineDataSet(entryList: List<Entry>, label: String): LineDataSet {
  return LineDataSet(entryList, label).apply {
    valueFormatter = GramsValueFormatter
    lineWidth = 6f
    setDrawCircles(true)
    setDrawFilled(false)
    setDrawValues(true)
    fillColor = Green.toArgb()
    color = Green.toArgb()
    valueTextColor = White.toArgb()
    valueTextSize = 14f
  }
}

private fun calculateAllTimeGramsDistribution(uses: List<Use>): List<Entry> {
  val dayBeforeFirstUseDate = uses.minBy { it.date }.localDate.toEpochDay()
  val daysSinceFirstUse = (dayBeforeFirstUseDate..now().toEpochDay()).toList()
  return daysSinceFirstUse.associateWith { uses.filter { u -> u.localDate.toEpochDay() == it } }.toSortedMap()
    .map { (k, v) ->
      Entry((k - dayBeforeFirstUseDate).toFloat(), v.totalGrams.toFloat())
    }
}
