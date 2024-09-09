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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun LineChart(
  datasets: List<LineDataSet>,
  description: String,
  range: Float? = null,
  configureXAxis: XAxis.() -> Unit
) {
  val colors = MaterialTheme.colors

  Box(Modifier.fillMaxWidth().aspectRatio(1f).padding(8.dp)) {
    AndroidView(::LineChart, Modifier.fillMaxSize()) { chart ->
      chart.description.text = description
      chart.description.textColor = colors.primary.toArgb()
      chart.legend.textColor = colors.primary.toArgb()

      chart.data = LineData(datasets)
      chart.notifyDataSetChanged()
      chart.invalidate()

      range?.let {
        chart.setVisibleXRange(range, range)
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
        setDrawLimitLinesBehindData(true)
        textColor = colors.primary.toArgb()
        axisLineColor = colors.primary.toArgb()
        configureXAxis(this)
      }
    }
  }
}
