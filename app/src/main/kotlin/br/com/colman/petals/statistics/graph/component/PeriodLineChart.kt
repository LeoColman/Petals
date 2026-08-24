package br.com.colman.petals.statistics.graph.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.grafima.charts.line.LineAxisConfig
import io.grafima.charts.line.LineChart
import io.grafima.charts.line.LineChartStyle
import io.grafima.charts.line.LineCrosshairConfig
import io.grafima.charts.line.LineCurveType
import io.grafima.charts.line.LineDataSet
import io.grafima.charts.line.LineSeries
import io.grafima.charts.line.LineValueLabelConfig
import io.grafima.charts.line.ReferenceLine

/**
 * Whichever series comes first supplies the x axis labels, so [series] leads with the readings and
 * not with anything decorative drawn alongside them.
 *
 * The shared frame for the Stats charts that compare selected periods against a fixed axis: the same
 * square, the same theme colours, and a legend naming each period.
 *
 * The x axis is always bounded by the calendar rather than by the data — twenty-four hours, seven
 * weekdays — so [xMin] and [xMax] are required rather than inferred.
 */
@Composable
@Suppress("LongParameterList")
fun PeriodLineChart(
  series: List<LineSeries>,
  contentDescription: String,
  xMin: Float,
  xMax: Float,
  maxXLabels: Int,
  xLabelFormatter: (Float) -> String,
  yLabelFormatter: (Float) -> String,
  referenceLines: List<ReferenceLine> = emptyList(),
  valueLabels: LineValueLabelConfig = LineValueLabelConfig(),
  curveType: LineCurveType = LineCurveType.Linear
) {
  val colors = MaterialTheme.colors

  Column(Modifier.fillMaxWidth().padding(8.dp)) {
    LineChart(
      dataSet = LineDataSet(series, contentDescription),
      modifier = Modifier.fillMaxWidth().aspectRatio(1f),
      style = LineChartStyle(showDots = true, curveType = curveType, valueLabels = valueLabels),
      axisConfig = LineAxisConfig(
        showVerticalGrid = true,
        gridColor = colors.primary.copy(alpha = GridAlpha),
        axisColor = colors.primary,
        labelColor = colors.primary,
        yMin = 0f,
        xMin = xMin,
        xMax = xMax,
        maxXLabels = maxXLabels,
        xLabelFormatter = xLabelFormatter,
        yLabelFormatter = yLabelFormatter,
        referenceLines = referenceLines
      ),
      // Off, as it is on every other chart in the app. On this one it would also offer a break period
      // band for selection, and a band is a region of the day rather than something that was measured.
      crosshairConfig = LineCrosshairConfig(enabled = false)
    )

    SeriesLegend(series)
  }
}

private const val GridAlpha = 0.3f

/**
 * Named series only. A break period draws one band per shaded range but wants a single legend entry,
 * so the bands past the first are deliberately unlabelled and belong nowhere in the legend.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SeriesLegend(series: List<LineSeries>) {
  val labelled = series.filter { it.label.isNotBlank() }
  if (labelled.isEmpty()) return

  FlowRow(
    Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    labelled.forEach { LegendEntry(it.color, it.label) }
  }
}

@Composable
private fun LegendEntry(swatch: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
    Box(Modifier.size(12.dp).background(swatch, CircleShape))
    Text(label, fontSize = 12.sp)
  }
}
