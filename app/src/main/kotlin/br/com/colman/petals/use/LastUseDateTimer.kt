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

package br.com.colman.petals.use

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R.string.*
import br.com.colman.petals.use.TimeUnit.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun LastUseDateTimer(lastUseDate: LocalDateTime) {
  val dateString = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(lastUseDate)

  var millis by remember { mutableStateOf(ChronoUnit.MILLIS.between(lastUseDate, now())) }

  LaunchedEffect(millis) {
    while (true) {
      delay(11)
      millis = ChronoUnit.MILLIS.between(lastUseDate, now())
    }
  }

  var millisCopy = millis
  val labels = listOf(Year, Month, Day, Hour, Minute, Second, Millisecond).map {
    val unitsInTotal = millisCopy / it.millis
    millisCopy -= unitsInTotal * it.millis
    it to unitsInTotal
  }

  Column(Modifier.padding(16.dp), Arrangement.spacedBy(16.dp)) {
    Column {
      Text(stringResource(quit_date_text))
      Text(dateString, fontSize = 24.sp)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      labels.forEach { (label, amount) ->

        Row(Modifier, Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
          Row(Modifier.weight(0.5f), Arrangement.SpaceBetween) {
            Text(stringResource(label.unitName))
            Text("$amount")
          }
          LinearProgressIndicator(
            amount.toFloat() / label.max,
            Modifier
              .height(8.dp)
              .weight(0.5f)
          )
        }
      }
    }
  }
}

@Suppress("MagicNumber")
private enum class TimeUnit(@StringRes val unitName: Int, val max: Long, val millis: Long) {
  Millisecond(milliseconds, 1000L, 1),
  Second(seconds, 60L, Millisecond.max),
  Minute(minutes, 60L, Second.max * Millisecond.max),
  Hour(hours, 24L, Minute.max * Minute.millis),
  Day(days, 31L, Hour.max * Hour.millis),
  Month(months, 12L, Day.max * Day.millis),
  Year(years, 60L, Month.max * Month.millis),
}
