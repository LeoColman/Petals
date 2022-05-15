/*
 * Petals APP
 * Copyright (C) 2021 Leonardo Colman Lopes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package br.com.colman.petals.hittimer

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import br.com.colman.petals.R
import br.com.colman.petals.R.color.darkGreen
import br.com.colman.petals.R.color.lightGreen
import br.com.colman.petals.R.string.*
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.apache.commons.lang3.time.DurationFormatUtils.formatDuration
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit.MILLIS

private const val tenSecondsMillis = 10_000L

@Preview
@Composable
fun ComposeHitTimer() {
  val hitTimer = remember { HitTimer() }

  val millisLeft by hitTimer.millisLeft.collectAsState(initial = tenSecondsMillis)

  Column(
    Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    spacedBy(24.dp), CenterHorizontally
  ) {
    Box(Modifier.padding(top = 60.dp)) {
      TimerText(millisLeft)
    }

    Column(Modifier.width(160.dp), spacedBy(8.dp)) {
      Button(onClick = { hitTimer.start() }, Modifier.fillMaxWidth()) {
      Text(stringResource(start), fontSize = 24.sp)
    }

      Button(onClick = { hitTimer.reset() }, Modifier.fillMaxWidth()) {
      Text(stringResource(reset), fontSize = 24.sp)
    }
    }
    WhyTenSeconds()
  }
}

@Composable
private fun TimerText(millisLeft: Long) {
  val isTimerRunning = millisLeft > 0L
  var blinking by remember { mutableStateOf(false) }

  LaunchedEffect(isTimerRunning) {
    if (isTimerRunning) {
      blinking = false; return@LaunchedEffect
    }

    repeat(7) {
      blinking = !blinking
      delay(150)
    }
  }

  val duration = HitTimer.duration(millisLeft)

  if (blinking) BlinkingText(duration) else NonBlinkingText(duration)
}

@Composable
private fun BlinkingText(text: String) {
  Text(text, fontSize = 60.sp, color = Red, modifier = Modifier.height(100.dp))
}

@Composable
private fun NonBlinkingText(text: String) {
  Text(text, fontSize = 52.sp, color = Black, modifier = Modifier.height(100.dp))
}

private class HitTimer {

  private var startDate: LocalDateTime? = null

  fun start() {
    startDate = now()
  }

  fun reset() {
    startDate = null
  }

  val millisLeft = flow {
    while (true) {
      delay(13)
      emit(calculateMillisLeft())
    }
  }

  private fun calculateMillisLeft(): Long {
    return startDate?.let { startDate ->
      val millisElapsed = startDate.until(now(), MILLIS)
      (tenSecondsMillis - millisElapsed).coerceAtLeast(0)
    } ?: tenSecondsMillis
  }

  companion object {
    fun duration(millis: Long): String = formatDuration(millis, "ss:SSS")
  }
}

@Preview
@Composable
private fun WhyTenSeconds() {
  Card(
    Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Column(
      Modifier
        .fillMaxWidth()
        .padding(16.dp),
      spacedBy(16.dp)
    ) {
      Text(stringResource(R.string.hittimer_why_ten_secs), fontSize = 24.sp)

      Text(stringResource(tenSecondsIntro), fontSize = 18.sp)

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
    verticalAxisTitle = context.resources.getString(subjectve_high)
    horizontalAxisTitle = context.getString(breathhold_duration)
  }
}

private fun subjectiveHighWeakSeries(context: Context): LineGraphSeries<DataPoint> {
  val datapoints = arrayOf(
    DataPoint(0.0, 30.0),
    DataPoint(10.0, 40.0),
    DataPoint(20.0, 35.0)
  )

  return LineGraphSeries(datapoints).apply {
    color = context.resources.getColor(lightGreen)
    isDrawDataPoints = true
    title = context.getString(_175thc)
  }
}

private fun subjectiveHighStrongSeries(context: Context): LineGraphSeries<DataPoint> {
  val datapoints = arrayOf(
    DataPoint(0.0, 37.0),
    DataPoint(10.0, 47.0),
    DataPoint(20.0, 43.0)
  )

  return LineGraphSeries(datapoints).apply {
    color = context.resources.getColor(darkGreen)
    isDrawDataPoints = true
    title = context.getString(_355thc)
  }
}
