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
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.getSystemService
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


@Preview
@Composable
fun ComposeHitTimer() {
  val hitTimer = remember { HitTimer() }

  val ctx = LocalContext.current
  val millisLeft by hitTimer.millisLeft.collectAsState(hitTimer.durationMillis)
  var shouldVibrate by remember { mutableStateOf(false) }

  if(millisLeft == 0L && shouldVibrate) {
    ctx.vibrate()
  }

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

      Row {
        Checkbox(shouldVibrate, { shouldVibrate = it } )
        Text("Vibrate on timer end")
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

private fun Context.vibrate() {
  val vibrator = getSystemService<Vibrator>()
  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val effect = VibrationEffect.createOneShot(500, DEFAULT_AMPLITUDE)
    vibrator?.vibrate(effect)
  } else {
    vibrator?.vibrate(500)
  }
}
