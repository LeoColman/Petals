package br.com.colman.petals.statistics.graph.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
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
    setDrawFilled(true)
    setDrawValues(true)
    fillColor = Color.Cyan.copy(alpha = 0.3f).toArgb()
    color = Color.Cyan.toArgb()
    valueTextColor = Gray.toArgb()
    valueTextSize = 14f
  }
}

private fun calculateAllTimeGramsDistribution(uses: List<Use>): List<Entry> {
  val firstUseDay = uses.minBy { it.date }.localDate.toEpochDay()
  val daysSinceFirstUse = (firstUseDay..now().toEpochDay()).toList()
  return daysSinceFirstUse.associateWith { uses.filter { u -> u.localDate.toEpochDay() == it } }.toSortedMap()
    .map { (k, v) ->
      Entry((k - firstUseDay).toFloat(), v.totalGrams.toFloat())
    }
}
