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
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import br.com.colman.petals.R
import br.com.colman.petals.R.string.amount_grams_short
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.repository.BlockRepository
import br.com.colman.petals.use.repository.BlockType
import br.com.colman.petals.use.repository.BlockType.AllTime
import br.com.colman.petals.use.repository.BlockType.ThisMonth
import br.com.colman.petals.use.repository.BlockType.ThisWeek
import br.com.colman.petals.use.repository.BlockType.ThisYear
import br.com.colman.petals.use.repository.BlockType.Today
import br.com.colman.petals.use.repository.BlockRepository
import br.com.colman.petals.use.repository.BlockType
import br.com.colman.petals.use.repository.Use
import compose.icons.TablerIcons
import compose.icons.tablericons.Eye
import compose.icons.tablericons.EyeOff
import compose.icons.tablericons.Scale
import compose.icons.tablericons.ZoomMoney
import org.koin.androidx.compose.get
import org.koin.compose.koinInject
import java.math.RoundingMode.HALF_UP
import java.time.DayOfWeek.MONDAY
import java.time.LocalDate.now

@Composable
fun StatsBlocks(uses: List<Use>) {
  val blockRepository = koinInject<BlockRepository>()

  val isTodayCensored by blockRepository.isTodayCensored.collectAsState(true)
  val isThisWeekCensored by blockRepository.isThisWeekCensored.collectAsState(true)
  val isThisMonthCensored by blockRepository.isThisMonthCensored.collectAsState(true)
  val isThisYearCensored by blockRepository.isThisYearCensored.collectAsState(true)
  val isAllTimeCensored by blockRepository.isAllTimeCensored.collectAsState(true)

  Row(Modifier.horizontalScroll(rememberScrollState()).width(Max)) {
    UseBlock(Modifier.weight(1f), Today, uses.filter { it.date.toLocalDate() == now() }, isTodayCensored)

    UseBlock(
      Modifier.weight(1f),
      ThisWeek,
      uses.filter { it.date.toLocalDate().with(MONDAY) == now().with(MONDAY) },
      isThisWeekCensored
    )

    UseBlock(Modifier.weight(1f), ThisMonth, uses.filter { it.date.month == now().month }, isThisMonthCensored)

    UseBlock(Modifier.weight(1f), ThisYear, uses.filter { it.date.year == now().year }, isThisYearCensored)

    UseBlock(Modifier.weight(1f), AllTime, uses, isAllTimeCensored)
  }
}

@Composable
private fun UseBlock(modifier: Modifier, blockType: BlockType, uses: List<Use>, isCensored: Boolean) {
  var totalGrams by remember { mutableStateOf("") }
  var totalCost by remember { mutableStateOf("") }

  val settingsRepository = koinInject<SettingsRepository>()
  val decimalPrecision by settingsRepository.decimalPrecision.collectAsState(settingsRepository.decimalPrecisionList[2])

  // HALF_UP is necessary because the default rounding
  // mode is "throw an exception".
  LaunchedEffect(uses) {
    totalGrams = uses.sumOf { it.amountGrams }.setScale(decimalPrecision, HALF_UP).toString()
    totalCost = uses.sumOf { it.costPerGram * it.amountGrams }.setScale(decimalPrecision, HALF_UP).toString()
  }

  UseBlock(modifier, blockType, totalGrams, totalCost, isCensored)
}

@Preview
@Composable
private fun UseBlock(
  modifier: Modifier = Modifier,
  blockType: BlockType = Today,
  totalGrams: String = "12.345",
  totalValue: String = "54.321",
  isCensored: Boolean = true
) {
  val settingsRepository = koinInject<SettingsRepository>()
  val blockRepository = koinInject<BlockRepository>()

  val currencyIcon by settingsRepository.currencyIcon.collectAsState("$")

  Card(modifier.padding(8.dp).defaultMinSize(145.dp), elevation = 4.dp) {
    Column(Modifier.padding(8.dp), spacedBy(4.dp)) {
      Row(Modifier.padding(8.dp).fillMaxWidth(), Center, CenterVertically) {
        Text(stringResource(blockType.resourceId), fontWeight = Bold)
        IconButton({ blockRepository.setBlockCensure(blockType, !isCensored) }) {
          CensureIcon(isCensored)
        }
      }

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(TablerIcons.ZoomMoney, null)
        BlockText("$currencyIcon $totalValue", isCensored)
      }

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(TablerIcons.Scale, null)
        BlockText(stringResource(amount_grams_short, totalGrams), isCensored)
      }
    }
  }
}

@Composable
private fun CensureIcon(isCensored: Boolean) {
  val chosenIcon = if (isCensored) TablerIcons.Eye else TablerIcons.EyeOff
  Icon(chosenIcon, null, Modifier.size(18.dp))
}

@Composable
private fun BlockText(blockText: String, isCensored: Boolean) {
  if (isCensored) Text(blockText) else Text(stringResource(R.string.censored))
}
