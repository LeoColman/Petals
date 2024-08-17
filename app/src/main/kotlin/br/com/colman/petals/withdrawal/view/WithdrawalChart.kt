package br.com.colman.petals.withdrawal.view

import android.content.Context
import android.graphics.Color
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import br.com.colman.petals.withdrawal.interpolator.Interpolator
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit.SECONDS

@Composable
@Suppress("LongParameterList")
fun WithdrawalChart(
  lastUseDate: LocalDateTime?,
  data: Map<Duration, Double>,
  graphTitle: Context.(currentValue: Double?) -> String,
  verticalAxisTitle: String,
  horizontalAxisTitle: String,
  maxX: Double,
  maxY: Double,
) {
  val colors = MaterialTheme.colorScheme
  val scaledData = data.scaled(maxY)

  val interpolator = Interpolator(scaledData)

  AndroidView({
    createGraph(it, verticalAxisTitle, horizontalAxisTitle, lastUseDate, colors, scaledData, maxX, maxY)
  }, update = {
    val currentValue = lastUseDate?.let { lud -> interpolator.value(lud.daysToTodayInSeconds().times(SecondsPerDay)) }
    it.title = graphTitle(it.context, currentValue)
    it.removeAllSeries()
    it.addSeries(scaledData.toLineGraphSeries())

    if (lastUseDate != null) it.addSeries(interpolator.currentPointSeries(lastUseDate))
    it.invalidate()
  })
}

private fun Map<Duration, Double>.scaled(maxY: Double): Map<Duration, Double> {
  return mapValues { ((it.value - values.min()) * maxY / (values.max() - values.min())) }
}

@Suppress("LongParameterList")
private fun createGraph(
  context: Context,
  verticalAxisTitle: String,
  horizontalAxisTitle: String,
  lastUseDate: LocalDateTime?,
  colors: ColorScheme,
  data: Map<Duration, Double>,
  maxX: Double,
  maxY: Double
) = GraphView(context).apply {
  val interpolator = Interpolator(data)

  addSeries(data.toLineGraphSeries())
  if (lastUseDate != null) addSeries(interpolator.currentPointSeries(lastUseDate))

  viewport.apply {
    isXAxisBoundsManual = true
    setMinX(-1.0)
    setMaxX(maxX)

    isYAxisBoundsManual = true
    setMinY(0.0)
    setMaxY(maxY)
  }

  gridLabelRenderer.apply {
    titleColor = colors.primary.toArgb()
    verticalAxisTitleColor = colors.primary.toArgb()
    horizontalAxisTitleColor = colors.primary.toArgb()
    horizontalLabelsColor = colors.primary.toArgb()
    verticalLabelsColor = colors.primary.toArgb()

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

private fun Interpolator.currentPointSeries(lastUseDate: LocalDateTime): PointsGraphSeries<DataPoint> {
  val daysQuit = lastUseDate.daysToTodayInSeconds()
  return PointsGraphSeries(arrayOf(DataPoint(daysQuit, value(daysQuit.times(SecondsPerDay))))).apply {
    size = 15f
    color = Color.parseColor("#059033")
    shape = PointsGraphSeries.Shape.POINT
  }
}

const val SecondsPerDay = 86400
private fun LocalDateTime.daysToTodayInSeconds() = SECONDS.between(this, now()).toDouble().div(SecondsPerDay)
