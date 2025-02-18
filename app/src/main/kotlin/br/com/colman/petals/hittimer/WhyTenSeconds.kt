package br.com.colman.petals.hittimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
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
import br.com.colman.petals.R.string.subjectve_high
import br.com.colman.petals.R.string.ten_seconds_introduction
import br.com.colman.petals.R.string.ten_seconds_source
import br.com.colman.petals.R.string.why_ten_seconds
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

@Preview
@Composable
fun WhyTenSeconds() {
  Card(Modifier.fillMaxWidth().padding(16.dp)) {
    Column(Modifier.fillMaxWidth().padding(16.dp), Arrangement.spacedBy(16.dp)) {
      Text(stringResource(why_ten_seconds), fontSize = 24.sp)
      Text(stringResource(ten_seconds_introduction), fontSize = 18.sp)

      Box(Modifier.height(300.dp)) {
        SubjectiveHigh()
      }

      Text(stringResource(ten_seconds_source), fontSize = 12.sp)
    }
  }
}

@Composable
fun SubjectiveHigh() {
  val colors = MaterialTheme.colors

  AndroidView({ context ->
    GraphView(context).apply {
      addSeries(
        LineGraphSeries(getSubjectiveHighWeakSeries().toTypedArray()).apply {
          color = ContextCompat.getColor(context, lightGreen)
          isDrawDataPoints = true
          title = context.getString(_175thc)
        }
      )

      addSeries(
        LineGraphSeries(getSubjectiveHighStrongSeries().toTypedArray()).apply {
          color = ContextCompat.getColor(context, darkGreen)
          isDrawDataPoints = true
          title = context.getString(_355thc)
        }
      )

      viewport.apply {
        isYAxisBoundsManual = true
        setMaxY(60.0)
        setMinY(0.0)
        isXAxisBoundsManual = true
        setMaxX(25.0)
        setMinX(0.0)
      }

      legendRenderer.apply {
        backgroundColor = colors.background.toArgb()
        textColor = colors.primary.toArgb()
        isVisible = true
      }

      gridLabelRenderer.apply {
        verticalLabelsColor = colors.primary.toArgb()
        horizontalLabelsColor = colors.primary.toArgb()
        horizontalAxisTitleColor = colors.primary.toArgb()
        verticalAxisTitleColor = colors.primary.toArgb()

        verticalAxisTitle = context.resources.getString(subjectve_high)
        horizontalAxisTitle = context.getString(breathhold_duration_seconds)
      }
    }
  })
}

fun getSubjectiveHighWeakSeries() = listOf(
  DataPoint(0.0, 30.0),
  DataPoint(10.0, 40.0),
  DataPoint(20.0, 35.0)
)

fun getSubjectiveHighStrongSeries() = listOf(
  DataPoint(0.0, 37.0),
  DataPoint(10.0, 47.0),
  DataPoint(20.0, 43.0)
)
