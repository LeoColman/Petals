package br.com.colman.petals.statistics.graph.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import info.appdev.charting.charts.LineChart
import info.appdev.charting.components.XAxis
import info.appdev.charting.data.EntryFloat
import info.appdev.charting.data.LineData
import info.appdev.charting.data.LineDataSet

@Composable
fun LineChart(
  datasets: List<LineDataSet<EntryFloat>>,
  description: String,
  visibleXRange: Float? = null,
  configureXAxis: XAxis.() -> Unit
) {
  val colors = MaterialTheme.colors

  Box(Modifier.fillMaxWidth().aspectRatio(1f).padding(8.dp)) {
    AndroidView(::LineChart, Modifier.fillMaxSize()) { chart ->
      chart.description.text = description
      chart.description.textColor = colors.primary.toArgb()
      chart.legend.textColor = colors.primary.toArgb()
      chart.setMaxVisibleValueCount(30)

      chart.data = LineData(datasets.toMutableList())
      chart.notifyDataSetChanged()
      chart.invalidate()

      visibleXRange?.let {
        chart.setVisibleXRange(it, it)
        chart.setVisibleXRangeMaximum(datasets[0].xMax)
        chart.moveViewToX(datasets[0].xMax)
        chart.notifyDataSetChanged()
      }

      chart.axisRight.isEnabled = false
      chart.axisLeft.apply {
        axisMinimum = 0f
        textColor = colors.primary.toArgb()
        axisLineColor = colors.primary.toArgb()
      }

      chart.xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        // AndroidChart builds a chart's XAxis before it reads the display density, so the first chart of the process
        // would keep these two defaults in raw pixels and draw tiny labels. Setting them again applies the density.
        textSize = 10f
        yOffset = 4f
        isDrawLimitLinesBehindData = true
        textColor = colors.primary.toArgb()
        axisLineColor = colors.primary.toArgb()
        configureXAxis(this)
      }
    }
  }
}
