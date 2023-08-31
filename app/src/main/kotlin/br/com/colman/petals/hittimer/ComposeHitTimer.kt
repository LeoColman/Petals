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
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import br.com.colman.petals.R.color.smokeColor
import br.com.colman.petals.R.string.reset
import br.com.colman.petals.R.string.start
import br.com.colman.petals.R.string.vibrate_on_timer_end
import br.com.colman.petals.settings.SettingsRepository
import kotlinx.coroutines.delay
import org.koin.androidx.compose.get

@Preview
@Composable
fun ComposeHitTimer(repository: HitTimerRepository = get()) {
  val hitTimer = rememberSaveable { HitTimer() }

  val ctx = LocalContext.current
  val millisLeft by hitTimer.millisLeft.collectAsState(hitTimer.durationMillis)
  val shouldVibrate by repository.shouldVibrate.collectAsState(false)

  val alpha = millisLeft.toFloat() / hitTimer.durationMillis
  val backgroundColor = colorResource(smokeColor).copy(1 - alpha)

  if (millisLeft == 0L && shouldVibrate) {
    ctx.vibrate()
  }

  Column(
    Modifier
      .fillMaxWidth()
      .background(backgroundColor)
      .verticalScroll(rememberScrollState()),
    spacedBy(24.dp),
    CenterHorizontally
  ) {
    Box(Modifier.padding(top = 60.dp)) {
      TimerText(millisLeft)
    }

    Column(Modifier.width(180.dp), spacedBy(8.dp)) {
      Button(onClick = { hitTimer.start() }, Modifier.fillMaxWidth()) {
        Text(stringResource(start), fontSize = 24.sp)
      }

      Button(onClick = { hitTimer.reset() }, Modifier.fillMaxWidth()) {
        Text(stringResource(reset), fontSize = 24.sp)
      }

      Row(Modifier.fillMaxWidth(), Start, CenterVertically) {
        Checkbox(shouldVibrate, { repository.setShouldVibrate(it) })
        Text(stringResource(vibrate_on_timer_end))
      }
    }
    WhyTenSeconds()
  }
}

@Composable
private fun TimerText(millisLeft: Long) {
  val isTimerRunning = millisLeft > 0L
  var blinking by remember { mutableStateOf(false) }

  val settingsRepository = get<SettingsRepository>()
  val hitTimerMillisecondsEnabled by settingsRepository.hitTimerMillisecondsEnabled.collectAsState(
    settingsRepository.hitTimerMillisecondsEnabledList[0]
  )

  LaunchedEffect(isTimerRunning) {
    if (isTimerRunning) {
      blinking = false
      return@LaunchedEffect
    }

    repeat(7) {
      blinking = !blinking
      delay(150)
    }
  }

  var duration = HitTimer.duration(millisLeft)
  if (hitTimerMillisecondsEnabled == "disabled") {
    duration = HitTimer.durationMillisecondsDisabled(millisLeft)
  }

  if (blinking) BlinkingText(duration) else NonBlinkingText(duration)
}

@Composable
private fun BlinkingText(text: String) {
  Text(text, fontSize = 60.sp, color = MaterialTheme.colors.error, modifier = Modifier.height(100.dp))
}

@Composable
private fun NonBlinkingText(text: String) {
  Text(text, fontSize = 52.sp, color = MaterialTheme.colors.primary, modifier = Modifier.height(100.dp))
}

@Suppress("DEPRECATION")
private fun Context.vibrate() {
  val vibrator = getSystemService<Vibrator>()
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val effect = VibrationEffect.createOneShot(500, DEFAULT_AMPLITUDE)
    vibrator?.vibrate(effect)
  } else {
    vibrator?.vibrate(500)
  }
}
