package br.com.colman.petals.statistics.graph.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.colman.petals.use.pause.repository.Pause
import io.grafima.charts.line.LineDataPoint
import io.grafima.charts.line.LineSeries
import io.grafima.charts.line.ReferenceLine
import io.grafima.charts.line.ReferenceLineAxis
import java.time.LocalTime

private val BreakColor = Color(0xFFFFC107) // Amber - warning
private val BreakFillColor = BreakColor.copy(alpha = 0.24f)

private fun LocalTime.toHourAxis() = hour + minute / 60f

/**
 * The shaded x-ranges (in hour-of-day axis units) covered by the enabled [pauses].
 * A pause crossing midnight (start > end) is split into two ranges: [start..23] and [0..end].
 * Pure geometry — no chart dependencies — so it is unit-testable.
 */
fun breakPeriodRanges(pauses: List<Pause>): List<Pair<Float, Float>> {
  return pauses.filter { it.isEnabled }.flatMap { pause ->
    val startX = pause.startTime.toHourAxis()
    val endX = pause.endTime.toHourAxis()
    if (pause.startTime > pause.endTime) {
      listOf(startX to 23f, 0f to endX)
    } else {
      listOf(startX to endX)
    }
  }
}

/**
 * The x positions (hour-of-day axis units) of every enabled pause's start and end, for the lines
 * marking the band edges. Pure geometry — unit-testable.
 */
fun breakPeriodEdges(pauses: List<Pause>): List<Float> {
  return pauses.filter { it.isEnabled }.flatMap { listOf(it.startTime.toHourAxis(), it.endTime.toHourAxis()) }
}

/**
 * The translucent band(s) standing over each enabled [Pause] on the per-hour graph, each filled from
 * [yMax] down to the axis. Only the first band carries [label], so the legend gets one "break period"
 * entry rather than one per band.
 */
fun createBreakPeriodBands(pauses: List<Pause>, yMax: Float, label: String): List<LineSeries> {
  return breakPeriodRanges(pauses).mapIndexed { index, (startX, endX) ->
    LineSeries(
      id = "break-$index",
      label = if (index == 0) label else "",
      points = listOf(LineDataPoint(startX, yMax), LineDataPoint(endX, yMax)),
      color = BreakColor,
      // Two identical colours rather than fillAlpha: the generated fill fades out towards the axis,
      // and a band that fades reads as data trailing off instead of as a block of time.
      fillGradientColors = listOf(BreakFillColor, BreakFillColor),
      strokeWidth = 0.dp,
      // The band is a region, not a reading. Dots on its two corners would invite reading them.
      dotRadius = 0.dp
    )
  }
}

/** A thin vertical rule at each enabled pause's start and end hour, marking the band edges. */
fun breakPeriodReferenceLines(pauses: List<Pause>): List<ReferenceLine> {
  return breakPeriodEdges(pauses).map { x ->
    ReferenceLine(value = x, axis = ReferenceLineAxis.X, color = BreakColor)
  }
}
