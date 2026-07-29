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
import br.com.colman.petals.R.string.holding_past_peak
import br.com.colman.petals.R.string.pause
import br.com.colman.petals.R.string.reset
import br.com.colman.petals.R.string.resume
import br.com.colman.petals.R.string.start
import br.com.colman.petals.R.string.vibrate_on_timer_end
import br.com.colman.petals.settings.SettingsRepository
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import java.util.Locale

@Preview
@Composable
fun ComposeHitTimer(repository: HitTimerRepository = koinInject()) {
  val hitTimer = rememberSaveable { HitTimer() }
  // While paused the elapsed value stops changing, so nothing recomposes: the label needs its own state.
  var isPaused by rememberSaveable { mutableStateOf(false) }

  val ctx = LocalContext.current
  val millisElapsed by hitTimer.millisElapsed.collectAsState(0L)
  val shouldVibrate by repository.shouldVibrate.collectAsState(false)

  val millisLeft = (hitTimer.durationMillis - millisElapsed).coerceAtLeast(0)
  val alpha = millisLeft.toFloat() / hitTimer.durationMillis
  val backgroundColor = colorResource(smokeColor).copy(1 - alpha)

  // Keyed on the crossing rather than checked inline: the elapsed counter keeps ticking past the
  // target, so an inline check would recompose and buzz every hundred milliseconds, forever.
  val hasReachedTarget = millisLeft == 0L
  LaunchedEffect(hasReachedTarget, shouldVibrate) {
    if (hasReachedTarget && shouldVibrate) ctx.vibrate()
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

    HoldOvertime(millisElapsed, hitTimer.durationMillis)

    Column(Modifier.width(180.dp), spacedBy(8.dp)) {
      TimerButtons(hitTimer, millisElapsed, isPaused) { isPaused = it }

      Row(Modifier.fillMaxWidth(), Start, CenterVertically) {
        Checkbox(shouldVibrate, { repository.setShouldVibrate(it) })
        Text(stringResource(vibrate_on_timer_end))
      }
    }
    WhyTenSeconds(millisElapsed / 1000.0)
  }
}

/**
 * Pause is what makes an unbounded elapsed counter usable: it freezes the reading so you can look at
 * it, where [HitTimer.reset] would wipe the very number you wanted. Disabled until there is something
 * to freeze.
 */
@Composable
private fun TimerButtons(
  hitTimer: HitTimer,
  millisElapsed: Long,
  isPaused: Boolean,
  onPausedChange: (Boolean) -> Unit
) {
  Button(
    onClick = {
      hitTimer.start()
      onPausedChange(false)
    },
    Modifier.fillMaxWidth()
  ) {
    Text(stringResource(start), fontSize = 24.sp)
  }

  Button(
    onClick = {
      if (isPaused) hitTimer.resume() else hitTimer.pause()
      onPausedChange(!isPaused)
    },
    Modifier.fillMaxWidth(),
    enabled = millisElapsed > 0
  ) {
    Text(stringResource(if (isPaused) resume else pause), fontSize = 24.sp)
  }

  Button(
    onClick = {
      hitTimer.reset()
      onPausedChange(false)
    },
    Modifier.fillMaxWidth()
  ) {
    Text(stringResource(reset), fontSize = 24.sp)
  }
}

/**
 * Only appears once the target is passed. Until then the countdown already tells you the hold; after
 * it, this is the only place the real hold time exists, and seeing it is the whole point.
 */
@Composable
private fun HoldOvertime(millisElapsed: Long, durationMillis: Long) {
  if (millisElapsed <= durationMillis) return

  Text(
    stringResource(holding_past_peak, "%.1f".format(Locale.US, millisElapsed / 1000.0)),
    fontSize = 20.sp,
    color = MaterialTheme.colors.error
  )
}

@Composable
private fun TimerText(millisLeft: Long) {
  val isTimerRunning = millisLeft > 0L
  var blinking by remember { mutableStateOf(false) }

  val settingsRepository = koinInject<SettingsRepository>()
  val hitTimerMillisecondsEnabled by settingsRepository.isHitTimerMillisecondsEnabled.collectAsState(true)

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

  var duration = HitTimer.formatDuration(millisLeft)
  if (!hitTimerMillisecondsEnabled) {
    duration = HitTimer.formatDurationShort(millisLeft)
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
