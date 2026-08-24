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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.statistics.graph.formatter.GramsLabel
import br.com.colman.petals.statistics.graph.formatter.gramsAxisFormatter
import io.grafima.charts.line.LineAxisConfig
import io.grafima.charts.line.LineChart
import io.grafima.charts.line.LineChartStyle
import io.grafima.charts.line.LineCrosshairConfig
import io.grafima.charts.line.LineCurveType
import io.grafima.charts.line.LineDataPoint
import io.grafima.charts.line.LineDataSet
import io.grafima.charts.line.LineSeries
import io.grafima.charts.line.LineValueLabelConfig
import io.grafima.charts.line.ReferenceLine

/**
 * Whichever series comes first supplies the x axis labels, so [series] leads with the readings and
 * not with anything decorative drawn alongside them.
 *
 * The shared frame for the Stats charts that compare selected periods against a fixed axis: the same
 * square, the same theme colours, a title, and a legend naming each period.
 *
 * The x axis is always bounded by the calendar rather than by the data — twenty-four hours, seven
 * weekdays — so [xMin] and [xMax] are required rather than inferred. The y axis is grams either way,
 * and is left to the chart to scale.
 */
@Composable
@Suppress("LongParameterList")
fun PeriodLineChart(
  title: String,
  series: List<LineSeries>,
  xMin: Float,
  xMax: Float,
  maxXLabels: Int,
  xLabelFormatter: (Float) -> String,
  referenceLines: List<ReferenceLine> = emptyList(),
  showValueLabels: Boolean = false,
  curveType: LineCurveType = LineCurveType.Linear
) {
  val colors = MaterialTheme.colors
  val dataMax = series.flatMap { it.points }.maxOfOrNull { it.y } ?: 0f

  // A chart with nothing logged has no range of its own. Left to scale itself it collapses every
  // point, gridline and label onto a single edge, which reads as "every hour maxed out" rather than
  // as an empty week. Pinning an empty chart to 0..1 puts the flat line along the bottom instead.
  //
  // With real data both bounds stay null on purpose: `includeZeroInYRange` already reaches down to
  // zero, and pinning either end would trade the chart's rounded tick values for its raw maximum
  // divided five ways.
  val isEmpty = dataMax <= 0f

  // Held across recompositions: a fresh formatter makes the whole axis config unequal, and the chart
  // then re-measures every label and repaints rather than skipping.
  val yLabelFormatter = remember(dataMax) { gramsAxisFormatter(dataMax) }
  val valueLabels = valueLabelConfig(showValueLabels, colors.onSurface)

  Column(Modifier.fillMaxWidth().padding(8.dp)) {
    Text(
      title,
      Modifier.fillMaxWidth(),
      color = colors.onSurface,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.subtitle2
    )

    LineChart(
      dataSet = LineDataSet(series, title),
      modifier = Modifier.fillMaxWidth().aspectRatio(1f),
      style = LineChartStyle(showDots = true, curveType = curveType, valueLabels = valueLabels),
      axisConfig = LineAxisConfig(
        showVerticalGrid = true,
        gridColor = colors.primary.copy(alpha = GridAlpha),
        axisColor = colors.primary,
        labelColor = colors.primary,
        yMin = if (isEmpty) 0f else null,
        yMax = if (isEmpty) 1f else null,
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

// The grid carries up to twenty-four verticals behind several overlapping series here, rather than
// the handful the other charts draw, and at full strength it competes with the readings.
private const val GridAlpha = 0.3f
private val ValueLabelSize = 12.sp

/** Top-level, so the config holding it stays equal across recompositions. */
private val GramsPointLabel: (LineSeries, LineDataPoint) -> String = { _, point -> GramsLabel(point.y) }

/**
 * Value labels in one theme colour rather than in each series' own. The period palette is built for
 * strokes, and several of its colours (green for today, yellow for a fortnight) are close to
 * unreadable as text on the light theme's background.
 */
@Composable
private fun valueLabelConfig(enabled: Boolean, color: Color) = remember(enabled, color) {
  LineValueLabelConfig(
    enabled = enabled,
    formatter = GramsPointLabel,
    textStyle = TextStyle(color = color, fontSize = ValueLabelSize, fontWeight = FontWeight.Medium)
  )
}

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
