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
import java.time.LocalDateTime.now

private fun calculateGramsDistributionPerDaySinceFirstUse(uses: List<Use>): List<Entry> {
  val dayBeforeFirstUseDate = uses.minByOrNull { it.date }!!.localDate.toEpochDay().dec()
  val now = now().toLocalDate().toEpochDay()
  val dateRange = (dayBeforeFirstUseDate..now).toList()

  val usesPerDay = dateRange.associateWith { uses.filter { u -> u.localDate.toEpochDay() == it } }

  return usesPerDay.mapValues { it.value.totalGrams }.toSortedMap().map { (k, v) ->
    Entry(
      (k - dayBeforeFirstUseDate).toFloat(),
      v.toFloat()
    )
  }
}

@Composable
fun createAllTimeDistribution(uses: List<Use>): LineDataSet {
  return LineDataSet(calculateGramsDistributionPerDaySinceFirstUse(uses), stringResource(R.string.all_time)).apply {
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
