package br.com.colman.petals.withdrawal.view

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import br.com.colman.petals.withdrawal.interpolator.Interpolator
import br.com.colman.petals.withdrawal.tolerance.SecondsPerDay
import io.grafima.charts.line.LineAxisConfig
import io.grafima.charts.line.LineChart
import io.grafima.charts.line.LineChartStyle
import io.grafima.charts.line.LineCrosshairConfig
import io.grafima.charts.line.LineCurveType
import io.grafima.charts.line.LineDataPoint
import io.grafima.charts.line.LineDataSet
import io.grafima.charts.line.LineSeries
import java.time.Duration

/**
 * The eight symptom curves are only comparable because every one of them is drawn on the same pinned
 * scale, so the axis bounds here are the point of the chart rather than a detail. `xMin` is -1
 * because one dataset carries a pre-abstinence baseline at day -1.
 */
@Composable
@Suppress("LongParameterList")
fun WithdrawalChart(
  effectiveAbstinence: Duration?,
  data: Map<Duration, Double>,
  graphTitle: Context.(currentValue: Double?) -> String,
  verticalAxisTitle: String,
  horizontalAxisTitle: String,
  maxX: Double,
  maxY: Double,
) {
  val colors = MaterialTheme.colors
  val context = LocalContext.current
  val scaledData = data.scaled(maxY)
  val interpolator = Interpolator(scaledData)

  val currentValue = effectiveAbstinence?.let { chartPointFor(interpolator, it, maxX).second }

  val curve = LineSeries(
    id = "curve",
    label = verticalAxisTitle,
    points = scaledData.map { (duration, value) ->
      LineDataPoint(duration.toDays().toFloat(), value.toFloat(), duration.toDays().toString())
    },
    color = colors.secondary
  )

  // "You are here" is a one-point series rather than an annotation, which is what the GraphView
  // version did too. Dots are on for the whole chart, so the marker reads as a point on the curve.
  val marker = effectiveAbstinence?.let {
    val (x, y) = chartPointFor(interpolator, it, maxX)
    LineSeries(
      id = "marker",
      label = "",
      points = listOf(LineDataPoint(x.toFloat(), y.toFloat(), "")),
      color = colors.primary
    )
  }

  Column(Modifier.fillMaxWidth()) {
    Text(
      context.graphTitle(currentValue),
      Modifier.fillMaxWidth(),
      color = colors.onSurface,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.subtitle2
    )

    LineChart(
      dataSet = LineDataSet(
        series = listOfNotNull(curve, marker),
        contentDescription = context.graphTitle(currentValue)
      ),
      modifier = Modifier.fillMaxWidth().weight(1f),
      // Linear, not the default cubic. The marker's y comes from a linear Interpolator, so a
      // smoothed curve bows away from the chord and leaves the dot floating off its own line.
      style = LineChartStyle(showDots = true, curveType = LineCurveType.Linear),
      axisConfig = LineAxisConfig(
        gridColor = colors.primary,
        axisColor = colors.primary,
        labelColor = colors.primary,
        yMin = 0f,
        yMax = maxY.toFloat(),
        xMin = -1f,
        xMax = maxX.toFloat(),
        xAxisTitle = horizontalAxisTitle,
        yAxisTitle = verticalAxisTitle
      ),
      // The marker already says where you are; a crosshair on top of a reference curve invites
      // reading a precision that the study data does not carry.
      crosshairConfig = LineCrosshairConfig(enabled = false)
    )
  }
}

/**
 * Each curve rescaled so its own minimum sits at 0 and its own maximum at [maxY]. The reported number is
 * therefore relative to the study's range, not an absolute score. A curve with no spread at all collapses
 * to zero rather than to NaN, which would silently render a blank chart.
 */
fun Map<Duration, Double>.scaled(maxY: Double): Map<Duration, Double> {
  val min = values.min()
  val max = values.max()
  if (max == min) return mapValues { 0.0 }
  return mapValues { ((it.value - min) * maxY / (max - min)) }
}

/**
 * Where the "you are here" dot sits: x in days, y interpolated from the curve. The x is clamped into the
 * chart's visible range, since [maxX] differs per chart and an unclamped dot is simply drawn out of view.
 * Pure - no Compose, no clock - so it is unit-testable.
 */
fun chartPointFor(interpolator: Interpolator, abstinence: Duration, maxX: Double): Pair<Double, Double> {
  val days = (abstinence.seconds.toDouble() / SecondsPerDay).coerceIn(0.0, maxX)
  return days to interpolator.value(days * SecondsPerDay)
}
