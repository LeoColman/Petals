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

package br.com.colman.petals.clock

import android.content.Context
import android.content.res.Resources.getSystem
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.colman.petals.R.string.days
import br.com.colman.petals.R.string.hours
import br.com.colman.petals.R.string.milliseconds
import br.com.colman.petals.R.string.minutes
import br.com.colman.petals.R.string.months
import br.com.colman.petals.R.string.quit_date_text
import br.com.colman.petals.R.string.seconds
import br.com.colman.petals.R.string.years
import br.com.colman.petals.clock.TimeUnit.Day
import br.com.colman.petals.clock.TimeUnit.Hour
import br.com.colman.petals.clock.TimeUnit.Millisecond
import br.com.colman.petals.clock.TimeUnit.Minute
import br.com.colman.petals.clock.TimeUnit.Month
import br.com.colman.petals.clock.TimeUnit.Second
import br.com.colman.petals.clock.TimeUnit.Year
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime
import org.joda.time.LocalDateTime.now
import org.joda.time.LocalDateTime.parse
import org.joda.time.Period
import org.joda.time.format.DateTimeFormat

class LastUseDateRepository(
  private val context: Context,
) : CoroutineScope by CoroutineScope(Default) {

  private val Context.datastore by preferencesDataStore("last_use")

  val quitDate = context.datastore.data.map { preferences ->
    preferences[stringPreferencesKey("lastUseDate")]?.let { parse(it) }
  }

  fun setQuitDate(date: LocalDateTime) {
    launch {
      context.datastore.edit { it[stringPreferencesKey("lastUseDate")] = date.toString() }
    }
  }


}

@Composable
fun LastUseDateTimerView(quitDate: LocalDateTime) {
  val locale = getSystem().configuration.locale
  val dateString = DateTimeFormat.longDateTime().withLocale(locale).print(quitDate)

  val periodSinceLastUse by flow {
    while (true) {
      delay(10)
      emit(Period(quitDate, now()))
    }
  }.collectAsState(null)

  val labels = periodSinceLastUse?.run {
    listOf(
      Year to years,
      Month to months,
      Day to days,
      Hour to hours,
      Minute to minutes,
      Second to seconds,
      Millisecond to millis
    )
  } ?: return

  Column(Modifier.padding(16.dp), Arrangement.spacedBy(16.dp)) {
    Column {
      Text(stringResource(quit_date_text))
      Text(dateString, fontSize = 24.sp)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      labels.forEach { (label, amount) ->

        Row(Modifier, Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
          Row(Modifier.weight(0.4f), Arrangement.SpaceBetween) {
            Text(stringResource(label.unitName))
            Text("$amount")
          }
          LinearProgressIndicator(
            amount.toFloat() / label.max,
            Modifier
              .height(8.dp)
              .weight(0.6f)
          )
        }
      }
    }
  }
}

@Suppress("MagicNumber")
private enum class TimeUnit(@StringRes val unitName: Int, val max: Int) {
  Year(years, 60),
  Month(months, 12),
  Day(days, 31),
  Hour(hours, 24),
  Minute(minutes, 60),
  Second(seconds, 60),
  Millisecond(milliseconds, 1000)
}
