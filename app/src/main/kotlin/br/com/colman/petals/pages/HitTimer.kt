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

import android.os.CountDownTimer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.apache.commons.lang3.time.DurationFormatUtils
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.MILLIS
import java.time.temporal.TemporalUnit

@Preview
@Composable
fun ComposeHitTimer() {
    var startTimerTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var millisLeft by remember { mutableStateOf(10_000L) }

    LaunchedEffect(startTimerTime) {
        while (true) {
            startTimerTime?.let {
                millisLeft = (10_000 - it.until(now(), MILLIS)).coerceAtLeast(0)
            }
            delay(10L)
        }
    }

    Column(
        Modifier.fillMaxWidth().padding(top = 60.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = spacedBy(24.dp)
    ) {
        val duration = DurationFormatUtils.formatDuration(millisLeft, "ss:SSS")
        Text("$duration", fontSize = 48.sp)

        Column(verticalArrangement = spacedBy(8.dp)) {
            Button(onClick = { startTimerTime = now() }, Modifier.width(160.dp)) {
                Text("Start", fontSize = 24.sp)
            }

            Button(
                onClick = { startTimerTime = null; millisLeft = 10_000L },
                Modifier.width(160.dp)
            ) {
                Text("Reset", fontSize = 24.sp)
            }
        }
    }
}
