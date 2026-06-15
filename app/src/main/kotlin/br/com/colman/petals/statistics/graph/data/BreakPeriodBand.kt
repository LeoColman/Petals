package br.com.colman.petals.statistics.graph.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import br.com.colman.petals.use.pause.repository.Pause
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalTime

private val BreakColor = Color(0xFFFFC107).toArgb() // Amber - warning
private const val BreakFillAlpha = 60

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
 * The x positions (hour-of-day axis units) of every enabled pause's start and end, for limit lines.
 * Pure geometry — unit-testable.
 */
fun breakPeriodEdges(pauses: List<Pause>): List<Float> {
  return pauses.filter { it.isEnabled }.flatMap { listOf(it.startTime.toHourAxis(), it.endTime.toHourAxis()) }
}

/**
 * Builds the translucent shaded band(s) representing each enabled [Pause] on the per-hour graph,
 * each filled down to the axis baseline (0). Only the first band carries [label] so a single
 * "break period" entry appears in the chart legend (avoiding overlapping per-line labels).
 */
fun createBreakPeriodBands(pauses: List<Pause>, yMax: Float, label: String): List<LineDataSet> {
  return breakPeriodRanges(pauses).mapIndexed { index, (startX, endX) ->
    bandDataset(startX, endX, yMax, if (index == 0) label else "")
  }
}

/**
 * A thin vertical [LimitLine] at each enabled pause's start and end hour, marking the band edges.
 */
fun breakPeriodLimitLines(pauses: List<Pause>): List<LimitLine> {
  return breakPeriodEdges(pauses).map { x ->
    LimitLine(x).apply {
      lineWidth = 1f
      lineColor = BreakColor
    }
  }
}

private fun bandDataset(startX: Float, endX: Float, yMax: Float, label: String): LineDataSet {
  val entries = listOf(Entry(startX, yMax), Entry(endX, yMax))
  return LineDataSet(entries, label).apply {
    setDrawCircles(false)
    setDrawValues(false)
    setDrawFilled(true)
    lineWidth = 0f
    color = BreakColor
    fillColor = BreakColor
    fillAlpha = BreakFillAlpha
    isHighlightEnabled = false
    form = if (label.isEmpty()) LegendForm.NONE else LegendForm.SQUARE
  }
}
