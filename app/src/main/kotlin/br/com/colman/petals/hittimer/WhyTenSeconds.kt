package br.com.colman.petals.hittimer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries

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

@Composable
fun SubjectiveHigh(holdSeconds: Double = 0.0) {
  val primaryColor = MaterialTheme.colors.primary.toArgb()

  AndroidView({ context ->
    GraphView(context).apply {
      // The curves are added here, not in update: GridLabelRenderer sizes the axis labels on the first
      // layout pass, and a GraphView that starts empty lays the horizontal title over the tick numbers.
      addCurve(getSubjectiveHighWeakSeries(), ContextCompat.getColor(context, lightGreen), context.getString(_175thc))
      addCurve(getSubjectiveHighStrongSeries(), ContextCompat.getColor(context, darkGreen), context.getString(_355thc))

      viewport.apply {
        isYAxisBoundsManual = true
        setMaxY(60.0)
        setMinY(0.0)
        isXAxisBoundsManual = true
        setMaxX(ChartMaxX)
        setMinX(0.0)
      }

      // Drawn in Compose instead: GraphView's legend paints a colour swatch for every series,
      // including the untitled hold markers, and its box sits on top of the curves.
      legendRenderer.isVisible = false

      gridLabelRenderer.apply {
        verticalLabelsColor = primaryColor
        horizontalLabelsColor = primaryColor
        horizontalAxisTitleColor = primaryColor
        verticalAxisTitleColor = primaryColor

        verticalAxisTitle = context.resources.getString(subjectve_high)
        horizontalAxisTitle = context.getString(breathhold_duration_seconds)
      }
    }
  }, update = { graph ->
    // Only the markers move, so only the markers are replaced. The curves stay as laid out.
    graph.series.filterIsInstance<PointsGraphSeries<DataPoint>>().forEach(graph::removeSeries)

    // Both markers in the accent colour, so "where you are" reads as one thing against the two curves.
    graph.addHoldMarker(getSubjectiveHighWeakSeries(), holdSeconds, primaryColor)
    graph.addHoldMarker(getSubjectiveHighStrongSeries(), holdSeconds, primaryColor)
  })
}

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

private fun GraphView.addCurve(points: List<DataPoint>, seriesColor: Int, seriesTitle: String) {
  addSeries(
    LineGraphSeries(points.toTypedArray()).apply {
      color = seriesColor
      isDrawDataPoints = true
      title = seriesTitle
    }
  )
}

/** Deliberately left untitled so it does not add a third entry to the legend. */
private fun GraphView.addHoldMarker(points: List<DataPoint>, holdSeconds: Double, markerColor: Int) {
  val (x, y) = holdPointOn(points, holdSeconds, ChartMaxX)
  addSeries(
    PointsGraphSeries(arrayOf(DataPoint(x, y))).apply {
      color = markerColor
      size = 15f
      shape = PointsGraphSeries.Shape.POINT
    }
  )
}

/**
 * Where a hold of [holdSeconds] sits on [points]: x clamped into the chart, y linearly interpolated
 * between the study's measurements. Pure - no Android, no Compose - so it is unit-testable.
 */
fun holdPointOn(points: List<DataPoint>, holdSeconds: Double, maxX: Double): Pair<Double, Double> {
  val sorted = points.sortedBy { it.x }
  val x = holdSeconds.coerceIn(sorted.first().x, minOf(maxX, sorted.last().x))

  val upperIndex = sorted.indexOfFirst { it.x >= x }.coerceAtLeast(1)
  val lower = sorted[upperIndex - 1]
  val upper = sorted[upperIndex]

  val span = upper.x - lower.x
  val y = if (span == 0.0) upper.y else lower.y + (upper.y - lower.y) * (x - lower.x) / span
  return x to y
}

/**
 * Source: Azorlosa JL, Greenwald MK, Stitzer ML. Marijuana smoking: effects of varying puff volume and breathhold
 * duration. J Pharmacol Exp Ther. 1995. Feb;272(2):560–9. PMID: 7853169.
 */
fun getSubjectiveHighWeakSeries() = listOf(
  DataPoint(0.0, 30.0),
  DataPoint(10.0, 40.0),
  DataPoint(20.0, 35.0)
)

/**
 * Source: Azorlosa JL, Greenwald MK, Stitzer ML. Marijuana smoking: effects of varying puff volume and breathhold
 * duration. J Pharmacol Exp Ther. 1995. Feb;272(2):560–9. PMID: 7853169.
 */
fun getSubjectiveHighStrongSeries() = listOf(
  DataPoint(0.0, 37.0),
  DataPoint(10.0, 47.0),
  DataPoint(20.0, 43.0)
)
