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

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.in_total_money
import br.com.colman.petals.R.string.total_grams
import compose.icons.TablerIcons
import compose.icons.tablericons.ChartInfographic
import compose.icons.tablericons.Scale
import compose.icons.tablericons.ZoomMoney
import java.time.LocalDate.now

@Composable
fun StatsBlocks(uses: List<Use>) {
  UseBlock("Today", uses.filter { it.date.toLocalDate() == now() })
}

@Composable
private fun UseBlock(title: String, uses: List<Use>) {
  var totalGrams by remember { mutableStateOf("") }
  var totalCost by remember { mutableStateOf("") }

  LaunchedEffect(totalGrams, totalCost) {
    totalGrams = uses.sumOf { it.amountGrams }.setScale(3).toString()
    totalCost = uses.sumOf { it.costPerGram * it.amountGrams }.setScale(3).toString()
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
  Card(Modifier.padding(8.dp)) {
    Column(Modifier, spacedBy(4.dp)) {
      Row(Modifier.padding(8.dp), spacedBy(4.dp), Bottom) {
        Icon(TablerIcons.ChartInfographic, null)
        Text(title, fontWeight = FontWeight.Bold)
      }

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(TablerIcons.ZoomMoney, null)
        Text(stringResource(in_total_money, "$$totalValue"))
      }

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {

        Icon(TablerIcons.Scale, null)
        Text(stringResource(total_grams, totalGrams))
      }
    }
  }
}