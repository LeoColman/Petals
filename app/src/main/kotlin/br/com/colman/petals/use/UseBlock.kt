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
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarTime
import compose.icons.tablericons.Scale
import compose.icons.tablericons.ZoomMoney
import java.time.LocalDate.now

@Composable
fun UseBlock(uses: List<Use>) {
  TodayUseBlock(uses.filter { it.date.toLocalDate() == now() })
}

@Preview
@Composable
private fun TodayUseBlock(
  todayUses: List<Use> = List(3) { Use(2.toBigDecimal(), 5.toBigDecimal()) }
) {
  val totalGrams = todayUses.sumOf { it.amountGrams }.setScale(3).toString()
  val totalCost = todayUses.sumOf { it.costPerGram * it.amountGrams }.setScale(3).toString()

  UseBlock(totalGrams, totalCost)
}

@Preview
@Composable
private fun UseBlock(
  totalGrams: String = "12.345",
  totalValue: String = "54.321"
) {
  Column(Modifier, spacedBy(4.dp)) {
    Row(Modifier.padding(8.dp), spacedBy(8.dp), CenterVertically) {
      Icon(TablerIcons.CalendarTime, null)
      Text("Today's Totals")
    }

    Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
      Icon(TablerIcons.ZoomMoney, null, Modifier.size(16.dp))
      Text("$$totalValue in total", fontSize = 10.sp)
    }

    Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {

      Icon(TablerIcons.Scale, null, Modifier.size(16.dp))
      Text("${totalGrams}g in total", fontSize = 10.sp)
    }
  }
}