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

package br.com.colman.petals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.MainActivity.TimeUnit.Day
import br.com.colman.petals.MainActivity.TimeUnit.Hour
import br.com.colman.petals.MainActivity.TimeUnit.Millisecond
import br.com.colman.petals.MainActivity.TimeUnit.Minute
import br.com.colman.petals.MainActivity.TimeUnit.Month
import br.com.colman.petals.MainActivity.TimeUnit.Second
import br.com.colman.petals.MainActivity.TimeUnit.Year
import br.com.colman.petals.clock.QuitTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime
import org.joda.time.LocalDateTime.now
import org.joda.time.Period
import org.joda.time.Period.ZERO
import org.joda.time.format.DateTimeFormat
import org.koin.android.ext.android.inject

@Suppress("FunctionName")
class MainActivity : ComponentActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private val quitTimer by inject<QuitTimer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val quitDate by quitTimer.quitDate.filterNotNull().collectAsState(null)
            Content(quitDate)
        }
    }

    @Composable
    fun Content(quitDate: LocalDateTime? = now()) {
        Column(Modifier.fillMaxWidth().padding(16.dp), spacedBy(16.dp), CenterHorizontally) {
            QuitButton()
            Text("Time since you've quit", fontSize = 18.sp)
            QuitTimerText(quitDate)
        }
    }

    @Composable
    fun QuitButton() {
        Button({ quit() }) {
            Text("Quit")
        }
    }

    private fun quit() {
        launch { quitTimer.setQuitDate(now()) }
    }

    @Composable
    fun QuitTimerText(quitDate: LocalDateTime? = now()) {
        if (quitDate == null) {
            Text("You haven't quit yet")
            return
        }
        Text("Quit date: ${DateTimeFormat.longDateTime().print(quitDate)}")
        val periodFromStopDate by quitTimer.periodFromStopDate.collectAsState(ZERO)
        Texts(periodFromStopDate)
    }

    @Preview
    @Composable
    fun Texts(periodFromStopDate: Period = Period.years(1)) {
        Column(verticalArrangement = spacedBy(8.dp)) {
            periodFromStopDate.run {
                listOf(
                    Year to years,
                    Month to months,
                    Day to days,
                    Hour to hours,
                    Minute to minutes,
                    Second to seconds,
                    Millisecond to millis
                )
            }.fold(0) { acc, (unit, amount) ->
                (acc + amount).also {
                    if (it > 0)
                        Row(horizontalArrangement = spacedBy(8.dp), verticalAlignment = CenterVertically) {
                            Text("$amount", Modifier.width(32.dp), textAlign = TextAlign.Center)
                            Text(unit.unit)
                            LinearProgressIndicator((amount.toFloat() / unit.max), Modifier.height(8.dp))
                        }
                }
            }
        }
    }

    @Suppress("MagicNumber")
    private enum class TimeUnit(val unit: String, val max: Int) {
        Year("Years", 60),
        Month("Months", 12),
        Day("Days", 31),
        Hour("Hours", 24),
        Minute("Minutes", 60),
        Second("Seconds", 60),
        Millisecond("Milliseconds", 1000)
    }
}
