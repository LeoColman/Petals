package br.com.colman.petals.statistics.graph

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.com.colman.petals.R.string.grams
import br.com.colman.petals.R.string.grams_distribution_per_hour_of_day
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalGrams
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
@Preview
fun UsePerHourGraphPreview() {
  val hoursInDay = (0..23).toList()
  val uses = List(293) {
    Use(LocalDate.now().atTime(hoursInDay.random(), 30), "3.37".toBigDecimal(), (it % 4).toBigDecimal())
  }

  UsePerHourGraph(uses)
}

val GramsFormatter = object : ValueFormatter() {
  override fun getPointLabel(entry: Entry): String {
    return "%.1f".format(entry.y) + "g"
  }

  override fun getAxisLabel(value: Float, axis: AxisBase): String {
    if(value.roundToInt() == 12) return "12"
    return (value.roundToInt() % 12).toString()
  }
}

@Composable
fun UsePerHourGraph(uses: List<Use>) {
  val grams = stringResource(grams)
  val description = stringResource(grams_distribution_per_hour_of_day)

  Box(Modifier.fillMaxWidth().aspectRatio(1f).padding(8.dp)) {
    AndroidView({ LineChart(it) }, Modifier.fillMaxSize()) { chart ->
      val gramsPerHour = calculateGramDistributionPerHour(uses)
      val gramsData = LineDataSet(gramsPerHour, grams).apply {
        valueFormatter = GramsFormatter
        setDrawFilled(true)
        setDrawCircles(true)
        setDrawValues(false)
        lineWidth = 3f
      }

      chart.description.text = description

      chart.data = LineData(gramsData)
      chart.notifyDataSetChanged()
      chart.invalidate()

      chart.axisRight.isEnabled = false

      chart.axisLeft.apply {
        axisMinimum = 0f
      }

      chart.xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        axisMinimum = 0f
        axisMaximum = 23.0f
        labelCount = 24
        granularity = 1.0f
        valueFormatter = GramsFormatter
      }
    }
  }
}

fun calculateGramDistributionPerHour(uses: List<Use>): List<Entry> {
  val hoursInDay = (0..23)
  val usesPerHourOfDay = hoursInDay.associateWith { uses.filter { a -> a.date.hour == it } }
  return usesPerHourOfDay.mapValues { it.value.totalGrams }.toSortedMap().map { (k, v) -> Entry(k.toFloat(), v.toFloat()) }
}

