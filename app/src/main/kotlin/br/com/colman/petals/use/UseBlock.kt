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

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.all_time
import br.com.colman.petals.R.string.amount_grams_short
import br.com.colman.petals.R.string.this_month
import br.com.colman.petals.R.string.this_week
import br.com.colman.petals.R.string.this_year
import br.com.colman.petals.R.string.today
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.repository.Use
import compose.icons.TablerIcons
import compose.icons.tablericons.Scale
import compose.icons.tablericons.ZoomMoney
import org.koin.androidx.compose.get
import java.math.RoundingMode.HALF_UP
import java.time.DayOfWeek.MONDAY
import java.time.LocalDate.now

@Composable
fun StatsBlocks(uses: List<Use>) {
  Row(Modifier.horizontalScroll(rememberScrollState())) {
    UseBlock(stringResource(today), uses.filter { it.date.toLocalDate() == now() })

    UseBlock(stringResource(this_week), uses.filter { it.date.toLocalDate().with(MONDAY) == now().with(MONDAY) })

    UseBlock(
      stringResource(this_month),
      uses.filter {
        it.date.month == now().month
      }
    )

    UseBlock(
      stringResource(this_year),
      uses.filter {
        it.date.year == now().year
      }
    )

    UseBlock(stringResource(all_time), uses)
  }
}

@Composable
private fun UseBlock(title: String, uses: List<Use>) {
  var totalGrams by remember { mutableStateOf("") }
  var totalCost by remember { mutableStateOf("") }

  // HALF_UP is necessary because the default rounding
  // mode is "throw an exception".
  LaunchedEffect(uses) {
    totalGrams = uses.sumOf { it.amountGrams }.setScale(3, HALF_UP).toString()
    totalCost = uses.sumOf { it.costPerGram * it.amountGrams }.setScale(3, HALF_UP).toString()
  }

  UseBlock(title, totalGrams, totalCost)
}

@Preview
@Composable
private fun UseBlock(
  title: String = "Today",
  totalGrams: String = "12.345",
  totalValue: String = "54.321"
) {
  val settingsRepository = get<SettingsRepository>()
  val currencyIcon by settingsRepository.currencyIcon.collectAsState("$")

  Card(
    Modifier
      .padding(8.dp)
      .defaultMinSize(145.dp),
    elevation = 4.dp
  ) {
    Column(Modifier.padding(8.dp), spacedBy(4.dp)) {
      Text(
        title,
        fontWeight = Bold,
        modifier = Modifier
          .padding(8.dp)
          .align(CenterHorizontally)
      )

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(TablerIcons.ZoomMoney, null)
        Text("$currencyIcon $totalValue")
      }

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(TablerIcons.Scale, null)
        Text(stringResource(amount_grams_short, totalGrams))
      }
    }
  }
}
