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

package br.com.colman.petals.pages

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R.string.reset
import br.com.colman.petals.R.string.start
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

  val millisLeft by hitTimer.millisLeft.collectAsState(10_000L)

  Column(Modifier.fillMaxWidth().padding(top = 60.dp), spacedBy(24.dp), CenterHorizontally) {
    TimerText(millisLeft)

    Column(Modifier.width(160.dp), spacedBy(8.dp)) {
      Button(onClick = { hitTimer.start() }, Modifier.fillMaxWidth()) {
        Text(stringResource(start), fontSize = 24.sp)
      }

      Button(onClick = { hitTimer.reset() }, Modifier.fillMaxWidth()) {
        Text(stringResource(reset), fontSize = 24.sp)
      }
    }
  }
}

@Composable
private fun TimerText(millisLeft: Long) {
  val duration = HitTimer.duration(millisLeft)
  Text(duration, fontSize = 52.sp)
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
    while(true) {
      delay(13)
      emit(calculateMillisLeft())
      println(calculateMillisLeft())
    }
  }

  private fun calculateMillisLeft(): Long {
    println(startDate)
    return startDate?.let { startDate ->
      val millisElapsed = startDate.until(now(), MILLIS)
      (tenSecondsMillis - millisElapsed).coerceAtLeast(0)
    } ?: tenSecondsMillis
  }

  private val tenSecondsMillis = 10_000L

  companion object {
    fun duration(millis: Long) = formatDuration(millis, "ss:SSS")
  }
}

