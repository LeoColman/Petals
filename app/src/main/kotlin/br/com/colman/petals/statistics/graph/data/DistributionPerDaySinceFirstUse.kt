package br.com.colman.petals.statistics.graph.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.string.grams_distribution_over_days_since_first_use
import br.com.colman.petals.statistics.graph.formatter.GramsValueFormatter
import br.com.colman.petals.use.repository.Use
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import java.math.BigDecimal
import java.time.LocalDate.now

@Composable
fun createAllTimeDistribution(uses: List<Use>): LineDataSet {
  val label = stringResource(grams_distribution_over_days_since_first_use)
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
  if (uses.isEmpty()) return emptyList()

  val usesByDay = uses.groupBy { it.localDate.toEpochDay() }

  val firstUseDay = usesByDay.keys.min()
  val lastUseDay = now().toEpochDay()

  return (firstUseDay..lastUseDay).map { day ->
    val totalGrams = usesByDay[day]?.sumOf { it.amountGrams } ?: BigDecimal.ZERO
    Entry((day - firstUseDay).toFloat(), totalGrams.toFloat())
  }
}
