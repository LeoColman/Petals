package br.com.colman.petals.statistics.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import br.com.colman.petals.R.string.grams
import br.com.colman.petals.R.string.grams_distribution_per_day_of_week
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalGrams
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
@Preview
fun UsePerDayOfWeekGraphPreview() {
  val uses = List(293) {
    Use(
      LocalDateTime.now().plusDays((1L..7L).toList().random()),
      "3.37".toBigDecimal(),
      (it % 4).toBigDecimal()
    )
  }

  UsePerDayOfWeekGraph(uses)
}

@Composable
fun UsePerDayOfWeekGraph(uses: List<Use>) {
  val grams = stringResource(grams)
  val description = stringResource(grams_distribution_per_day_of_week)
  val colors = MaterialTheme.colors
  val locale = LocalContext.current.resources.configuration.locales[0]

  Box(
    Modifier
      .fillMaxWidth()
      .aspectRatio(1f)
  ) {
    AndroidView(::HorizontalBarChart, Modifier.fillMaxSize()) { chart ->
      val gramsPerHour = calculateGramDistributionPerDayOfWeek(uses)
      val gramsData = BarDataSet(gramsPerHour, grams)

      chart.description.text = description
      chart.description.textColor = colors.primary.toArgb()
      chart.legend.textColor = colors.primary.toArgb()

      chart.xAxis.apply {
        valueFormatter = DayOfWeekFormatter(locale)
        axisMinimum = 1.0f
        axisMaximum = 7.0f
        granularity = 1.0f
        labelCount = 7

        textColor = colors.primary.toArgb()
        axisLineColor = colors.primary.toArgb()
      }

      chart.axisLeft.isEnabled = false
      chart.axisRight.apply {
        axisMinimum = 0f

        textColor = colors.primary.toArgb()
        axisLineColor = colors.primary.toArgb()
      }

      chart.data = BarData(gramsData).apply {
        barWidth = 0.5f
      }

      chart.setVisibleXRangeMinimum(7f)
      chart.notifyDataSetChanged()
      chart.invalidate()
    }
  }
}

private fun calculateGramDistributionPerDayOfWeek(uses: List<Use>): List<BarEntry> {
  val usesPerDayOfWeek = uses.groupBy { it.localDate.dayOfWeek }

  return usesPerDayOfWeek.mapValues { it.value.totalGrams }.toSortedMap()
    .map { (k, v) -> BarEntry(k.value.toFloat(), v.toFloat()) }
}

class DayOfWeekFormatter(private val locale: Locale) : ValueFormatter() {
  override fun getAxisLabel(value: Float, axis: AxisBase?): String {
    return DayOfWeek.of(value.toInt()).getDisplayName(TextStyle.FULL, locale)
  }
}
