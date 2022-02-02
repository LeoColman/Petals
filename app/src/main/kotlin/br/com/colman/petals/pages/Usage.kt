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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.colman.petals.use.*
import org.koin.androidx.compose.get

@Composable
fun Usage(useRepository: UseRepository = get()) {
  val lastUseDate by useRepository.getLastUseDate().collectAsState(null)

  Column(Modifier.verticalScroll(rememberScrollState()), spacedBy(8.dp), CenterHorizontally) {
    lastUseDate?.let { LastUseDateTimer(it) }

    AddUseButton { useRepository.insert(it) }

    val uses by useRepository.all().collectAsState(emptyList())
    if(uses.isEmpty()) return
    StatsBlocks(uses)
    UseCards(uses, { useRepository.insert(it) }, { useRepository.delete(it) })
  }
}

