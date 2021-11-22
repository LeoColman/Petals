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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.colman.petals.clock.QuitTimer
import br.com.colman.petals.clock.QuitTimerView
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime
import org.koin.androidx.compose.get

@Composable
fun Home(quitTimer: QuitTimer = get()) {
  val quitDate by quitTimer.quitDate.filterNotNull().collectAsState(null)

  Column(Modifier, Arrangement.spacedBy(8.dp), Alignment.CenterHorizontally) {
    quitDate?.let { QuitTimerView(it) }
    QuitButton()
  }
}

@Composable
fun QuitButton(quitTimer: QuitTimer = get()) {
  val scope = rememberCoroutineScope()
  fun quit() {
    scope.launch { quitTimer.setQuitDate(LocalDateTime.now()) }
  }

  Button(::quit) { Text("Quit") }
}