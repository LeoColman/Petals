package br.com.colman.petals.withdrawal.view

import android.content.Context
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import br.com.colman.petals.withdrawal.interpolator.Interpolator
import br.com.colman.petals.withdrawal.tolerance.SecondsPerDay
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import java.time.Duration

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
  val scaledData = data.scaled(maxY)

  val interpolator = Interpolator(scaledData)

  AndroidView({
    createGraph(it, verticalAxisTitle, horizontalAxisTitle, effectiveAbstinence, colors, scaledData, maxX, maxY)
  }, update = {
    val currentValue = effectiveAbstinence?.let { abstinence -> chartPointFor(interpolator, abstinence, maxX).second }
    it.title = graphTitle(it.context, currentValue)
    it.removeAllSeries()
    it.addSeries(scaledData.toLineGraphSeries().apply { color = colors.secondary.toArgb() })

    if (effectiveAbstinence != null) {
      it.addSeries(
        currentPointSeries(interpolator, effectiveAbstinence, maxX).apply { color = colors.primary.toArgb() }
      )
    }
    it.invalidate()
  })
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

@Suppress("LongParameterList")
private fun createGraph(
  context: Context,
  verticalAxisTitle: String,
  horizontalAxisTitle: String,
  effectiveAbstinence: Duration?,
  colors: Colors,
  data: Map<Duration, Double>,
  maxX: Double,
  maxY: Double
) = GraphView(context).apply {
  val interpolator = Interpolator(data)

  addSeries(data.toLineGraphSeries())
  if (effectiveAbstinence != null) addSeries(currentPointSeries(interpolator, effectiveAbstinence, maxX))

  viewport.apply {
    isXAxisBoundsManual = true
    setMinX(-1.0)
    setMaxX(maxX)

    isYAxisBoundsManual = true
    setMinY(0.0)
    setMaxY(maxY)
  }

  gridLabelRenderer.apply {
    titleColor = colors.onSurface.toArgb()
    verticalAxisTitleColor = colors.onSurface.toArgb()
    horizontalAxisTitleColor = colors.onSurface.toArgb()
    horizontalLabelsColor = colors.primary.toArgb()
    verticalLabelsColor = colors.primary.toArgb()
    gridColor = colors.primary.toArgb()

    this.verticalAxisTitle = verticalAxisTitle
    this.horizontalAxisTitle = horizontalAxisTitle
  }
}

private fun Map<Duration, Double>.toLineGraphSeries(): LineGraphSeries<DataPoint> {
  val dataPoints = this.map { (key, value) -> DataPoint(key.toDays().toDouble(), value) }
  return LineGraphSeries(dataPoints.toTypedArray()).apply {
    isDrawDataPoints = true
    dataPointsRadius = 8f
  }
}

private fun currentPointSeries(
  interpolator: Interpolator,
  abstinence: Duration,
  maxX: Double
): PointsGraphSeries<DataPoint> {
  val (x, y) = chartPointFor(interpolator, abstinence, maxX)
  return PointsGraphSeries(arrayOf(DataPoint(x, y))).apply {
    size = 15f
    shape = PointsGraphSeries.Shape.POINT
  }
}
