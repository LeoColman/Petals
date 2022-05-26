package br.com.colman.petals.statistics.graph

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.com.colman.petals.R.string.grams
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalGrams
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import me.moallemi.tools.daterange.localdate.LocalDateRange
import java.math.BigDecimal

//@Composable
//@Preview
//fun UsePerDayOfPeriodPreview() {
//  val hoursInDay = (0..23).toList()
//  val uses = List(293) {
//    Use(LocalDate.now().atTime(hoursInDay.random(), 30), "3.37".toBigDecimal(), (it % 4).toBigDecimal())
//  }
//
//  UsePerDayOfPeriod(uses)
//}

fun calculateDistributionInPeriod(uses: List<Use>, period: LocalDateRange): Map<Int, BigDecimal> {
  val dates = period.toList()
  val datesAndUses = dates.withIndex().associate { (i, dt) -> i to uses.filter { a -> a.localDate == dt }.totalGrams }
  return datesAndUses
}

@Composable
fun UsePerDayOfPeriod(
  period: LocalDateRange,
  usesCurrent: List<Use>,
  usesPrevious: List<Use>
) {
  val periodSize = period.toList().size.toLong() - 1
  val previousPeriod = LocalDateRange(period.start.minusDays(periodSize), period.endInclusive.minusDays(periodSize))

  val currentUsePeriod = calculateDistributionInPeriod(usesCurrent, period)
  val previousUsePeriod = calculateDistributionInPeriod(usesPrevious, previousPeriod)

  val grams = stringResource(grams)
  val description = "f"

  Box(Modifier.fillMaxWidth().aspectRatio(1f).padding(8.dp)) {
    AndroidView({ LineChart(it) }, Modifier.fillMaxSize()) { chart ->
      val currentUseData = currentUsePeriod.map { (k, v) -> Entry(k.toFloat(), v.toFloat()) }
      val previousUseData = previousUsePeriod.map { (k, v) -> Entry(k.toFloat(), v.toFloat()) }

      val currentDataSet = LineDataSet(currentUseData, "$periodSize days period").apply {
        valueFormatter = GramsFormatter
        color = Color.GREEN
        lineWidth = 5f
        setDrawCircles(false)
      }

      val previousDataSet = LineDataSet(previousUseData, "Previous $periodSize period").apply {
        setDrawCircles(false)
        lineWidth = 5f
        enableDashedLine(50f, 20f, 0f)
      }


      chart.description.text = description

      chart.data = LineData(currentDataSet, previousDataSet)
      chart.notifyDataSetChanged()
      chart.invalidate()

      chart.xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        axisMaximum = 0f
        axisMaximum = periodSize.toFloat()
        granularity = 1.0f
      }
    }
  }
}
