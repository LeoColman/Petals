package br.com.colman.petals.statistics.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.com.colman.petals.R.plurals.last_x_days
import br.com.colman.petals.R.string.grams_distribution_per_hour_of_day
import br.com.colman.petals.statistics.component.Period
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalGrams
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt

@Composable
@Preview
fun UsePerHourGraphPreview() {
  val hoursInDay = (0..23).toList()
  val minutesInHour = (0..59).toList()
  val uses = List(293) {
    Use(
      LocalDate.now().atTime(hoursInDay.random(), minutesInHour.random()),
      "3.37".toBigDecimal(),
      (it % 4).toBigDecimal()
    )
  }

  UsePerHourGraph(mapOf(Period.TwoWeek to uses))
}

@Composable
@Preview
fun UsePerHourGraphPreview2() {
  val hoursInDay = (0..23).toList()
  val minutesInHour = (0..59).toList()
  val uses = List(293) {
    Use(
      LocalDate.now().minusDays(hoursInDay.random().toLong()).atTime(hoursInDay.random(), minutesInHour.random()),
      "3.37".toBigDecimal(),
      (it % 4).toBigDecimal()
    )
  }
  val uses2 = List(29) {
    Use(
      LocalDate.now().atTime(hoursInDay.random(), minutesInHour.random()),
      "3.37".toBigDecimal(),
      (it % 4).toBigDecimal()
    )
  }

  UsePerHourGraph(mapOf(Period.Week to uses, Period.TwoWeek to uses2, Period.Month to uses + uses2))
}

val GramsValueFormatter = IValueFormatter { _, entry, _, _ -> "%.1f".format(entry.y) + "g" }

val GramsFormatter = object : IAxisValueFormatter {
  override fun getFormattedValue(value: Float, axis: AxisBase?): String {
    if (value.roundToInt() == 12) return "12"
    return (value.roundToInt() % 12).toString()
  }
}

@Composable
fun UsePerHourGraph(useGroups: Map<Period, List<Use>>) {
  val description = stringResource(grams_distribution_per_hour_of_day)
  val colors = MaterialTheme.colors

  Box(
    Modifier
      .fillMaxWidth()
      .aspectRatio(1f)
      .padding(8.dp)
  ) {
    AndroidView(::LineChart, Modifier.fillMaxSize()) { chart ->
      val gramsDatas = useGroups.map { (period, uses) ->
        val label = chart.context.resources.getQuantityString(last_x_days, period.days ?: 0, period.days ?: 0)
        createDataset(period.days ?: 0, uses, label)
      }

      chart.description.text = description
      chart.description.textColor = colors.primary.toArgb()
      chart.legend.textColor = colors.primary.toArgb()

      chart.data = LineData(gramsDatas)
      chart.notifyDataSetChanged()
      chart.invalidate()

      chart.axisRight.isEnabled = false

      chart.axisLeft.apply {
        axisMinimum = 0f

        textColor = colors.primary.toArgb()
        axisLineColor = colors.primary.toArgb()
      }

      chart.xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        axisMinimum = 0f
        axisMaximum = 23.0f
        labelCount = 24
        granularity = 1.0f
        valueFormatter = GramsFormatter
        addLimitLine(LimitLine(LocalTime.now().hour.toFloat()).apply {
          lineWidth = 2f
        })
        setDrawLimitLinesBehindData(true)

        textColor = colors.primary.toArgb()
        axisLineColor = colors.primary.toArgb()
      }
    }
  }
}

private fun calculateGramDistributionPerHour(uses: List<Use>): List<Entry> {
  val hoursInDay = (0..23)
  val usesPerHourOfDay = hoursInDay.associateWith { uses.filter { a -> a.date.hour == it } }
  return usesPerHourOfDay.mapValues { it.value.totalGrams }
    .toSortedMap().map { (k, v) -> Entry(k.toFloat(), v.toFloat()) }
}

private fun createDataset(days: Int, uses: List<Use>, label: String): LineDataSet {
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

val colors = mapOf(
  0 to Color.Green,
  7 to Color.Blue,
  14 to Color.Yellow,
  30 to Color.Red,
  60 to Color.Gray,
  90 to Color.DarkGray
).withDefault { Color.Red }

fun createColor(days: Int): Color {
  return colors.getValue(days)
}
