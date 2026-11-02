package br.com.colman.petals.statistics.graph.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R
import br.com.colman.petals.R.string.grams_distribution_over_days_since_first_use
import br.com.colman.petals.statistics.graph.formatter.GramsValueFormatter
import br.com.colman.petals.use.repository.Use
import info.appdev.charting.data.EntryFloat
import info.appdev.charting.data.LineDataSet
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDate.now

@Composable
fun createAllTimeDistributionWithMovingAverage(uses: List<Use>): List<LineDataSet<EntryFloat>> {
  val gramsLabel = stringResource(grams_distribution_over_days_since_first_use)
  val gramsDataSet = createAllTimeLineDataSet(calculateAllTimeGramsDistribution(uses), gramsLabel)

  val movingAverageLabel = stringResource(R.string.moving_average_grams_distribution)
  val movingAverageDataSet = createAllTimeAverageDataSet(
    calculateMovingAverage(calculateAllTimeGramsDistribution(uses)),
    movingAverageLabel
  )

  return listOf(gramsDataSet, movingAverageDataSet)
}

fun createAllTimeLineDataSet(entryList: List<EntryFloat>, label: String): LineDataSet<EntryFloat> {
  return LineDataSet(entryList.toMutableList(), label).apply {
    valueFormatter = GramsValueFormatter
    lineWidth = 3f
    isDrawFilled = true
    isDrawCircles = true
    isDrawCircleHoleEnabled = true
    fillColor = Color.Cyan.copy(alpha = 0.3f).toArgb()
    color = Color.Cyan.toArgb()
    valueTextColor = Gray.toArgb()
    valueTextSize = 14f
    lineMode = LineDataSet.Mode.CUBIC_BEZIER
  }
}

fun createAllTimeAverageDataSet(entryList: List<EntryFloat>, label: String): LineDataSet<EntryFloat> {
  return LineDataSet(entryList.toMutableList(), label).apply {
    isDrawValues = false
    isDrawCircles = false
    lineWidth = 3f
    color = Color.Green.toArgb()
    lineMode = LineDataSet.Mode.CUBIC_BEZIER
    enableDashedLine(10f, 5f, 0f)
  }
}

internal fun calculateAllTimeGramsDistribution(uses: List<Use>, today: LocalDate = now()): List<EntryFloat> {
  if (uses.isEmpty()) return emptyList()

  val usesByDay = uses.groupBy { it.localDate.toEpochDay() }

  val firstUseDay = usesByDay.keys.min()
  val lastUseDay = today.toEpochDay()

  return (firstUseDay..lastUseDay).map { day ->
    val totalGrams = usesByDay[day]?.sumOf { it.amountGrams } ?: BigDecimal.ZERO
    EntryFloat((day - firstUseDay).toFloat(), totalGrams.toFloat())
  }
}

internal fun calculateMovingAverage(entries: List<EntryFloat>): List<EntryFloat> {
  if (entries.isEmpty()) return emptyList()

  val result = mutableListOf<EntryFloat>()
  for (i in entries.indices) {
    val start = maxOf(0, i - 7 + 1)
    val end = i + 1
    val windowEntries = entries.subList(start, end)

    val averageValue = windowEntries.map { it.y }.average().toFloat()
    result.add(EntryFloat(entries[i].x, averageValue))
  }
  return result
}
