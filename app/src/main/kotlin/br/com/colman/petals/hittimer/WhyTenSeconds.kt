package br.com.colman.petals.hittimer

import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R.color.darkGreen
import br.com.colman.petals.R.color.lightGreen
import br.com.colman.petals.R.string._175thc
import br.com.colman.petals.R.string._355thc
import br.com.colman.petals.R.string.breathhold_duration_seconds
import br.com.colman.petals.R.string.current_hold
import br.com.colman.petals.R.string.subjectve_high
import br.com.colman.petals.R.string.ten_seconds_introduction
import br.com.colman.petals.R.string.ten_seconds_source
import br.com.colman.petals.R.string.why_ten_seconds
import io.grafima.charts.line.LineAnimationConfig
import io.grafima.charts.line.LineAxisConfig
import io.grafima.charts.line.LineChart
import io.grafima.charts.line.LineChartStyle
import io.grafima.charts.line.LineCrosshairConfig
import io.grafima.charts.line.LineCurveType
import io.grafima.charts.line.LineDataPoint
import io.grafima.charts.line.LineDataSet
import io.grafima.charts.line.LineSeries
import kotlin.math.roundToInt

/** [holdSeconds] is how long the current hit has been held, marked on both curves as it runs. */
@Preview
@Composable
fun WhyTenSeconds(holdSeconds: Double = 0.0) {
  Card(Modifier.fillMaxWidth().padding(16.dp)) {
    Column(Modifier.fillMaxWidth().padding(16.dp), Arrangement.spacedBy(16.dp)) {
      Text(stringResource(why_ten_seconds), fontSize = 24.sp)
      Text(stringResource(ten_seconds_introduction), fontSize = 18.sp)

      Box(Modifier.height(300.dp)) {
        SubjectiveHigh(holdSeconds)
      }
      SubjectiveHighLegend()

      Text(stringResource(ten_seconds_source), fontSize = 12.sp)
    }
  }
}

private const val ChartMaxX = 25.0
private const val ChartMaxY = 60.0

/** Twice the chart's own dots, which is roughly where the GraphView marker sat against its curves. */
private val MarkerDotRadius = 6.dp

@Composable
fun SubjectiveHigh(holdSeconds: Double = 0.0) {
  val colors = MaterialTheme.colors
  val weak = getSubjectiveHighWeakSeries()
  val strong = getSubjectiveHighStrongSeries()

  val verticalAxisTitle = stringResource(subjectve_high)
  val horizontalAxisTitle = stringResource(breathhold_duration_seconds)

  val curves = listOf(
    curve("weak", stringResource(_175thc), weak, colorResource(lightGreen)),
    curve("strong", stringResource(_355thc), strong, colorResource(darkGreen))
  )

  // Both markers in the accent colour, so "where you are" reads as one thing against the two curves.
  // A one-point series rather than an annotation, matching the withdrawal charts. Sized past the
  // curve dots as well as coloured apart: the marker is the thing you look for while holding.
  val markers = listOf(weak, strong).mapIndexed { index, points ->
    val (x, y) = holdPointOn(points, holdSeconds, ChartMaxX)
    LineSeries(
      id = "marker-$index",
      label = "",
      points = listOf(LineDataPoint(x.toFloat(), y.toFloat(), "")),
      color = colors.primary,
      dotRadius = MarkerDotRadius
    )
  }

  LineChart(
    dataSet = LineDataSet(series = curves + markers, contentDescription = verticalAxisTitle),
    modifier = Modifier.fillMaxSize(),
    // Linear, not the default cubic: holdPointOn interpolates linearly between the study's three
    // measurements, so a smoothed curve would leave the marker floating beside its own line.
    style = LineChartStyle(showDots = true, curveType = LineCurveType.Linear),
    axisConfig = LineAxisConfig(
      gridColor = colors.primary,
      // Off by default here, on in the GraphView this replaces. Worth keeping: the whole point of the
      // chart is where ten seconds falls, and that is much easier to read against a vertical rule.
      showVerticalGrid = true,
      axisColor = colors.primary,
      labelColor = colors.primary,
      yMin = 0f,
      yMax = ChartMaxY.toFloat(),
      xMin = 0f,
      xMax = ChartMaxX.toFloat(),
      xAxisTitle = horizontalAxisTitle,
      yAxisTitle = verticalAxisTitle
    ),
    // The marker already says where you are, and the curve is three measured points; a crosshair
    // invites reading a precision the study does not carry.
    crosshairConfig = LineCrosshairConfig(enabled = false),
    // Snapped, which is this library's way of saying "no animation". Two reasons, and either alone
    // would be enough: the marker tracks a running timer, so a morph would always be drawing where
    // the hold was rather than where it is; and an animation that never settles keeps Compose busy,
    // which stalls the timer's own recomposition under test.
    animationConfig = LineAnimationConfig(
      entrySpec = snap(),
      morphSpec = snap(),
      staggerMs = 0,
      startDelayMs = 0,
      seriesStaggerMs = 0
    )
  )
}

private fun curve(id: String, label: String, points: List<SubjectiveHighPoint>, color: Color) = LineSeries(
  id = id,
  label = label,
  // The point's own label is what ends up under the x axis, not the axis formatter, so these are
  // whole seconds. Left as `toString()` they read "10.0", and the study measured at 0, 10 and 20.
  points = points.map { LineDataPoint(it.seconds.toFloat(), it.high.toFloat(), "${it.seconds.roundToInt()}") },
  color = color
)

/**
 * Drawn in Compose rather than by the chart, because the two hold markers are untitled series and a
 * generated legend would either name them or leave them out, and "current hold" is worth naming.
 */
@Composable
private fun SubjectiveHighLegend() {
  Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
    LegendEntry(colorResource(lightGreen), stringResource(_175thc))
    LegendEntry(colorResource(darkGreen), stringResource(_355thc))
    LegendEntry(MaterialTheme.colors.primary, stringResource(current_hold))
  }
}

@Composable
private fun LegendEntry(swatch: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
    Box(Modifier.size(12.dp).background(swatch, CircleShape))
    Text(label, fontSize = 12.sp)
  }
}

/** One measurement from the study: a breathhold of [seconds] against the subjective high reported. */
data class SubjectiveHighPoint(val seconds: Double, val high: Double)

/**
 * Where a hold of [holdSeconds] sits on [points]: x clamped into the chart, y linearly interpolated
 * between the study's measurements. Pure - no Android, no Compose - so it is unit-testable.
 */
fun holdPointOn(points: List<SubjectiveHighPoint>, holdSeconds: Double, maxX: Double): Pair<Double, Double> {
  val sorted = points.sortedBy { it.seconds }
  val x = holdSeconds.coerceIn(sorted.first().seconds, minOf(maxX, sorted.last().seconds))

  val upperIndex = sorted.indexOfFirst { it.seconds >= x }.coerceAtLeast(1)
  val lower = sorted[upperIndex - 1]
  val upper = sorted[upperIndex]

  val span = upper.seconds - lower.seconds
  val y = if (span == 0.0) upper.high else lower.high + (upper.high - lower.high) * (x - lower.seconds) / span
  return x to y
}

/**
 * Source: Azorlosa JL, Greenwald MK, Stitzer ML. Marijuana smoking: effects of varying puff volume and breathhold
 * duration. J Pharmacol Exp Ther. 1995. Feb;272(2):560–9. PMID: 7853169.
 */
fun getSubjectiveHighWeakSeries() = listOf(
  SubjectiveHighPoint(0.0, 30.0),
  SubjectiveHighPoint(10.0, 40.0),
  SubjectiveHighPoint(20.0, 35.0)
)

/**
 * Source: Azorlosa JL, Greenwald MK, Stitzer ML. Marijuana smoking: effects of varying puff volume and breathhold
 * duration. J Pharmacol Exp Ther. 1995. Feb;272(2):560–9. PMID: 7853169.
 */
fun getSubjectiveHighStrongSeries() = listOf(
  SubjectiveHighPoint(0.0, 37.0),
  SubjectiveHighPoint(10.0, 47.0),
  SubjectiveHighPoint(20.0, 43.0)
)
