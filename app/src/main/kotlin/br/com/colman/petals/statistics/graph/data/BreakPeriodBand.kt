package br.com.colman.petals.statistics.graph.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import br.com.colman.petals.use.pause.repository.Pause
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalTime

private val BreakFillColor = Color.Gray.copy(alpha = 0.2f).toArgb()

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
 * Builds the translucent shaded band(s) representing each enabled [Pause] on the per-hour graph.
 * Each band is filled down to the axis baseline (0).
 */
fun createBreakPeriodBands(pauses: List<Pause>, yMax: Float): List<LineDataSet> {
  return breakPeriodRanges(pauses).map { (startX, endX) -> bandDataset(startX, endX, yMax) }
}

/**
 * A thin vertical [LimitLine] at each enabled pause's start and end hour, marking the band edges.
 */
fun breakPeriodLimitLines(pauses: List<Pause>): List<LimitLine> {
  return breakPeriodEdges(pauses).map { x ->
    LimitLine(x).apply {
      lineWidth = 1f
      lineColor = BreakFillColor
    }
  }
}

private fun bandDataset(startX: Float, endX: Float, yMax: Float): LineDataSet {
  val entries = listOf(Entry(startX, yMax), Entry(endX, yMax))
  return LineDataSet(entries, "").apply {
    setDrawCircles(false)
    setDrawValues(false)
    setDrawFilled(true)
    lineWidth = 0f
    color = Color.Transparent.toArgb()
    fillColor = BreakFillColor
    fillAlpha = 255
    isHighlightEnabled = false
    form = LegendForm.NONE
  }
}
