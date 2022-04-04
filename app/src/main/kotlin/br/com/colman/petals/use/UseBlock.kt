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

import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.*
import br.com.colman.petals.use.repository.Use
import compose.icons.FontAwesomeIcons
import compose.icons.Octicons
import compose.icons.TablerIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.regular.CalendarAlt
import compose.icons.fontawesomeicons.solid.CalendarWeek
import compose.icons.octicons.Stopwatch24
import compose.icons.tablericons.*
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import me.moallemi.tools.daterange.localdate.LocalDateRange
import me.moallemi.tools.daterange.localdate.rangeTo
import timber.log.Timber
import java.math.BigDecimal.ZERO
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.temporal.WeekFields
import java.util.*

//FIXME find me a better name
sealed interface MyInterface {
  @get:StringRes val stringRes: Int
  val period: LocalDateRange

  val dates: List<LocalDate>
    get() = period.toList()
}

object Today : MyInterface {
  override val period = now()..now()
  override val stringRes = today
}

object ThisWeek : MyInterface {
  override val period = now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1L)..now()
  override val stringRes = this_week
}

object ThisMonth : MyInterface {
  override val period = now().withDayOfMonth(1)..now()
  override val stringRes = this_month
}

object ThisYear : MyInterface {
  override val period = now().withDayOfYear(1)..now()
  override val stringRes = this_year
}

data class AllTime(val firstUse: LocalDate) : MyInterface {
  override val period = firstUse..now()
  override val stringRes = all_time
}

@Composable
fun presets(firstUse: LocalDate?) = listOf(Today, ThisWeek, ThisMonth, ThisYear, AllTime(firstUse ?: now()))

@Composable
fun SpecialButton(
  currentSelected: MyInterface,
  thisButton: MyInterface,
  onSelectedChange: (MyInterface) -> Unit
) {

  val color = if (thisButton == currentSelected) Color.Red else Color.White
  val colors = ButtonDefaults.buttonColors(backgroundColor = color)
  Button(onClick = { onSelectedChange(thisButton) }, colors = colors) {
  Text(stringResource(thisButton.stringRes))
}
}

@Preview
@Composable
fun StatsBlocks(
  uses: List<Use> = listOf(
    Use(now().atStartOfDay(), 12.34.toBigDecimal(), 12.3.toBigDecimal()),
    Use(now().atStartOfDay().minusDays(1), 1.34.toBigDecimal(), 12.34.toBigDecimal()),
    Use(now().atStartOfDay().minusDays(20), 12.3.toBigDecimal(), 58.34.toBigDecimal()),
    Use(now().atStartOfDay().minusDays(300), 12.34.toBigDecimal(), 1.34.toBigDecimal()),
    Use(now().atStartOfDay().minusDays(370), 767.4.toBigDecimal(), 13.4.toBigDecimal()),
  )
) {
  val presets = presets(uses.minOfOrNull { it.localDate })
  var currentSelected by remember { mutableStateOf(presets[0]) }

  Column(Modifier.padding(8.dp)) {
    presets.windowed(3, 3, true).forEach { ls ->
      Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
        ls.forEach {
          SpecialButton(currentSelected, it) { currentSelected = it }
        }
      }
    }

    val calendarState = rememberSelectableCalendarState(
      initialSelection = currentSelected.dates,
      initialSelectionMode = SelectionMode.Period
    )

    calendarState.selectionState.selection = currentSelected.dates

    SelectableCalendar(
      calendarState = calendarState
    )

    val scrollState = rememberScrollState(80)
    val usesInPeriod = uses.filter { it.localDate in currentSelected.dates }
    Row(Modifier.horizontalScroll(scrollState)) {
      UseBlock(stringResource(currentSelected.stringRes), usesInPeriod)
//        AverageCostBlock(currentSelected.dates, uses)

      AverageUseBlock(currentSelected.dates, uses)
    }
  }
}

@Composable
fun AverageCostBlock(period: List<LocalDate>, uses: List<Use>) {
  val uses = uses.filter { it.localDate in period }

  Card(
    Modifier
      .padding(8.dp)
      .defaultMinSize(200.dp),
    elevation = 4.dp
  ) {

    Column {
      Text(
        "Average Cost",
        Modifier
          .padding(8.dp)
          .align(CenterHorizontally),
        fontWeight = Bold
      )

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(Octicons.Stopwatch24, null, Modifier.size(24.dp))
        Text("\$ 17.355/hour")
      }

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(Icons.Default.Today, null, Modifier.size(24.dp))
        Text("\$ 70.12/day")
      }

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(FontAwesomeIcons.Solid.CalendarWeek, null, Modifier.size(24.dp))
        Text("\$ 215.35/week")
      }

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(FontAwesomeIcons.Regular.CalendarAlt, null, Modifier.size(24.dp))
        Text("\$ 725.55/month")
      }

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(TablerIcons.CalendarPlus, null, Modifier.size(24.dp))
        Text("\$ 1444.05/year")
      }
    }
  }
}

@Composable
fun AverageUseBlock(period: List<LocalDate>, uses: List<Use>) {
  val uses = uses.filter { it.localDate in period }
  if (uses.size < 2) return
  val totalGrams = uses.sumOf { it.amountGrams }

  Card(
    Modifier
      .padding(8.dp)
      .defaultMinSize(200.dp),
    elevation = 4.dp
  ) {

    Column {
      Text(
        "Average Use",
        Modifier
          .padding(8.dp)
          .align(CenterHorizontally),
        fontWeight = Bold
      )

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        val daysInPeriod = period.size
        val gramsPerDay = totalGrams / daysInPeriod.toBigDecimal()

        Icon(Icons.Default.Today, null, Modifier.size(24.dp))
        Text("$gramsPerDay g/day")
      }

      val weeksInPeriod = period.size / 7
      if (weeksInPeriod > 0) {
        Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
          val gramsPerWeek = totalGrams / weeksInPeriod.toBigDecimal()
          Icon(FontAwesomeIcons.Solid.CalendarWeek, null, Modifier.size(24.dp))
          Text("$gramsPerWeek g/week")
        }
      }

      val monthsInPeriod = period.size / 30
      if (monthsInPeriod > 0) {
        Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
          val gramsPerMonth = totalGrams / monthsInPeriod.toBigDecimal()
          Icon(FontAwesomeIcons.Regular.CalendarAlt, null, Modifier.size(24.dp))
          Text("$gramsPerMonth g/month")
        }
      }

      val yearsInPeriod = period.size / 365
      if (yearsInPeriod > 0) {
        Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
          val gramsPerYear = totalGrams / yearsInPeriod.toBigDecimal()
          Icon(TablerIcons.CalendarPlus, null, Modifier.size(24.dp))
          Text("$gramsPerYear g/year")
        }
      }
    }
  }
}

@Composable
private fun UseBlock(title: String, uses: List<Use>) {
  // HALF_UP is necessary because the default rounding
  // mode is "throw an exception".
  val totalGrams = uses.sumOf { it.amountGrams }.setScale(3, HALF_UP)
  val totalCost = uses.sumOf { it.costPerGram * it.amountGrams }.setScale(3, HALF_UP)
  val avgPerGram = if (totalCost.compareTo(ZERO) == 0) {
    Double.POSITIVE_INFINITY
  } else {
    (totalGrams / totalCost).setScale(3, HALF_UP)
  }

  Timber.d("Uses $uses")
  Timber.d("Calculating %s totalGrams, %s totalCost, %s avgPerGram", totalGrams, totalCost, avgPerGram)

  UseBlock(title, "$totalGrams", "$totalCost", "$avgPerGram")
}

@Preview
@Composable
private fun UseBlock(
  title: String = "Today",
  totalGrams: String = "12.345",
  totalValue: String = "54.321",
  avgPerGram: String = "77.77"
) {
  Card(
    Modifier
      .padding(8.dp)
      .defaultMinSize(145.dp),
    elevation = 4.dp
  ) {
    Column(Modifier.padding(8.dp), spacedBy(4.dp)) {
      val currencySymbol = stringResource(currency_symbol)
      val currencyIcon = CurrencyIcon(currencySymbol)

      Text(
        title, fontWeight = Bold,
        modifier = Modifier
          .padding(8.dp)
          .align(CenterHorizontally)
      )

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(currencyIcon, null)
        Text(totalValue)
      }

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(TablerIcons.Scale, null)
        Text(stringResource(amount_grams_short, totalGrams))
      }

      Row(Modifier.padding(8.dp), spacedBy(4.dp), CenterVertically) {
        Icon(TablerIcons.Percentage, null, Modifier.size(24.dp))
        Text(stringResource(cost_per_gram_short, currencySymbol + " " + avgPerGram))
      }
    }
  }
}

fun CurrencyIcon(symbol: String) = when (symbol) {
  "R$" -> TablerIcons.CurrencyReal
  else -> TablerIcons.CurrencyDollar
}
