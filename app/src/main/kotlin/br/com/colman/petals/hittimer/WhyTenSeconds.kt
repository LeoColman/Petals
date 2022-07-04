@file:Suppress("MagicNumber")
package br.com.colman.petals.hittimer

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import br.com.colman.petals.R
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

@Preview
@Composable
fun WhyTenSeconds() {
  Card(
    Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Column(
      Modifier
        .fillMaxWidth()
        .padding(16.dp),
      Arrangement.Absolute.spacedBy(16.dp)
    ) {
      Text(stringResource(R.string.hittimer_why_ten_secs), fontSize = 24.sp)

      Text(stringResource(R.string.tenSecondsIntro), fontSize = 18.sp)

      SubjectiveHigh()
      Text(stringResource(R.string.hittime_source), fontSize = 12.sp)
    }
  }
}

@Preview
@Composable
private fun SubjectiveHigh() {
  Box(Modifier.height(300.dp)) {
    AndroidView({ createGraph(it) })
  }
}

// TODO sync graph and timer
private fun createGraph(context: Context) = GraphView(context).apply {
  addSeries(subjectiveHighWeakSeries(context))
  addSeries(subjectiveHighStrongSeries(context))

  viewport.apply {
    isYAxisBoundsManual = true
    setMaxY(60.0)
    setMinY(0.0)
    isXAxisBoundsManual = true
    setMaxX(25.0)
    setMinX(0.0)
  }

  legendRenderer.apply {
    backgroundColor = Color.WHITE
    isVisible = true
  }

  gridLabelRenderer.apply {
    verticalAxisTitle = context.resources.getString(R.string.subjectve_high)
    horizontalAxisTitle = context.getString(R.string.breathhold_duration)
  }
}

private fun subjectiveHighWeakSeries(context: Context): LineGraphSeries<DataPoint> {
  val datapoints = arrayOf(
    DataPoint(0.0, 30.0),
    DataPoint(10.0, 40.0),
    DataPoint(20.0, 35.0)
  )

  return LineGraphSeries(datapoints).apply {
    color = context.resources.getColor(R.color.lightGreen)
    isDrawDataPoints = true
    title = context.getString(R.string._175thc)
  }
}

private fun subjectiveHighStrongSeries(context: Context): LineGraphSeries<DataPoint> {
  val datapoints = arrayOf(
    DataPoint(0.0, 37.0),
    DataPoint(10.0, 47.0),
    DataPoint(20.0, 43.0)
  )

  return LineGraphSeries(datapoints).apply {
    color = context.resources.getColor(R.color.darkGreen)
    isDrawDataPoints = true
    title = context.getString(R.string._355thc)
  }
}
