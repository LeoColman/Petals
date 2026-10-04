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

package br.com.colman.petals.navigation

import android.content.Context
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.filter_by_description_or_strain
import br.com.colman.petals.R.string.with_a_friend
import br.com.colman.petals.review.ReviewAppRequester
import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.use.AddUseButton
import br.com.colman.petals.use.AddUseFlow
import br.com.colman.petals.use.AddUseRequest
import br.com.colman.petals.use.LastUseDateTimer
import br.com.colman.petals.use.PauseCards
import br.com.colman.petals.use.StatsBlocks
import br.com.colman.petals.use.UseCards
import br.com.colman.petals.use.pause.PauseButton
import br.com.colman.petals.use.pause.repository.PauseRepository
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.widgets.updateWidget
import compose.icons.TablerIcons
import compose.icons.tablericons.ListSearch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.time.LocalTime
import kotlin.time.Duration.Companion.seconds

@Composable
fun Usage(
  useRepository: UseRepository = koinInject(),
  pauseRepository: PauseRepository = koinInject(),
  reviewAppRequester: ReviewAppRequester = koinInject()
) {
  val lastUseDate by useRepository.getLastUseDate().collectAsState(null)
  val lastUse = useRepository.getLastUse().collectAsState(null)
  val context = LocalContext.current

  var currentTime by remember {
    mutableStateOf(LocalTime.now())
  }
  LaunchedEffect(lastUse.value) {
    lastUse.value?.let {
      context.updateWidget(it)
    }
  }

  LaunchedEffect(Unit) {
    while (true) {
      val now = LocalTime.now()
      if (currentTime.minute != now.minute) {
        currentTime = now
      }
      delay(10.seconds)
    }
  }

  val pauses by pauseRepository.getAll().collectAsState(listOf())
  val isAnyPauseActive by remember { derivedStateOf { pauses.any { it.isActive(currentTime) } } }
  var addUseRequest by remember { mutableStateOf<AddUseRequest?>(null) }
  val usesWithStrains by remember(useRepository) { useRepository.allWithStrains() }.collectAsState(emptyList())
  val strains = remember(usesWithStrains) { usesWithStrains.mapNotNull { it.second }.associateBy { it.id } }
  fun strainOf(use: Use?) = use?.strainId?.let(strains::get)

  Column(
    Modifier
      .verticalScroll(rememberScrollState())
      .testTag("UsageMainColumn"),
    spacedBy(8.dp),
    CenterHorizontally
  ) {
    lastUseDate?.let { LastUseDateTimer(it) }

    Row(Modifier.padding(8.dp), spacedBy(8.dp), CenterVertically) {
      AddUseButton(isAnyPauseActive) { addUseRequest = AddUseRequest.from(lastUse.value, strainOf(lastUse.value)) }
      PauseButton(pauseRepository)
    }

    AddUseFlow(addUseRequest, isAnyPauseActive, reviewAppRequester, useRepository) { addUseRequest = null }

    PauseCards(pauseRepository)

    var filter by remember { mutableStateOf("") }
    val uses = remember(usesWithStrains, filter) {
      usesWithStrains.filter { (use, strain) -> use.matchesFilter(filter, strain) }.map { it.first }
    }

    StatsBlocks(uses)
    UsageFilter(filter) { filter = it }
    val scope = rememberCoroutineScope()
    UseCards(
      uses,
      strains,
      onEditUse = { scope.launch { updateUse(useRepository, it, context) } },
      onDeleteUse = { scope.launch { fetchCountAndUpdateWidget(useRepository, context, it) } },
      onDuplicateUse = { addUseRequest = AddUseRequest.from(it, strainOf(it)) }
    )
  }
}

/** Whether [this] use shows under a filter for [text]: its notes or its [strain]'s name contain it, ignoring case. */
internal fun Use.matchesFilter(text: String, strain: Strain?): Boolean =
  description.contains(text, ignoreCase = true) || strain?.name?.contains(text, ignoreCase = true) == true

private suspend fun updateUse(
  useRepository: UseRepository,
  use: Use,
  context: Context
) {
  useRepository.upsert(use)
  context.updateWidget(use)
}

suspend fun fetchCountAndUpdateWidget(useRepository: UseRepository, context: Context, use: Use) {
  useRepository.delete(use)
  val data = useRepository.countAll().first()
  if (data == 0) {
    context.updateWidget(null)
  }
}

@Composable
private fun UsageFilter(value: String, onValueChange: (String) -> Unit) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    leadingIcon = { Icon(TablerIcons.ListSearch, null) },
    label = { Text(stringResource(filter_by_description_or_strain)) },
    placeholder = { Text(stringResource(with_a_friend)) }
  )
}
