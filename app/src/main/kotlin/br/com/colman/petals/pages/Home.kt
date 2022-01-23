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

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.add_use
import br.com.colman.petals.clock.LastUseDateTimerView
import br.com.colman.petals.use.Use
import br.com.colman.petals.use.UseRepository
import org.koin.androidx.compose.get

@Composable
fun Home(useRepository: UseRepository = get()) {
  val quitDate by useRepository.lastUseDate.collectAsState(null)

  Column(Modifier, spacedBy(8.dp), CenterHorizontally) {
    quitDate?.let { LastUseDateTimerView(it) }

    AddUseButton { useRepository.insert(Use()) }
  }
}

@Preview
@Composable
fun AddUseButton(onTouch: () -> Unit = {}) {
  Button(onTouch) { Text(stringResource(add_use)) }
}
