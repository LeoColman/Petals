@file:Suppress("MagicNumber")
package br.com.colman.petals.statistics.card

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.plurals.amount_days
import br.com.colman.petals.R.plurals.amount_uses
import br.com.colman.petals.R.string.amount_grams
import br.com.colman.petals.R.string.average_per
import br.com.colman.petals.R.string.currency_symbol
import br.com.colman.petals.R.string.day
import br.com.colman.petals.R.string.month
import br.com.colman.petals.R.string.week
import br.com.colman.petals.R.string.year
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalCost
import br.com.colman.petals.use.repository.totalGrams
import br.com.colman.petals.utils.pluralResource
import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarStats
import compose.icons.tablericons.ChartPie
import me.moallemi.tools.daterange.localdate.LocalDateRange
import java.math.BigDecimal
import java.time.LocalDate.now

@Preview
@Composable
fun AverageUseCardPreview() {
  val hoursInDay = (0..23).toList()
  val minutesInHour = (0..59).toList()
  val uses = List(293) {
    Use(
      now().minusDays(it.toLong()).atTime(hoursInDay.random(), minutesInHour.random()),
      "3.37".toBigDecimal(),
      (it % 4).toBigDecimal()
    )
  }

  AverageUseCard(uses, LocalDateRange(now().minusDays(293L), now()))
}

@Composable
fun AverageUseCard(
  uses: List<Use>,
  period: LocalDateRange
) {
  if (uses.isEmpty()) return
  Column(Modifier.padding(8.dp), spacedBy(8.dp)) {
    Title(uses.count(), period)

    AverageList(uses, period)
  }
}

@Composable
private fun Title(uses: Int, period: LocalDateRange) {
  val amountDays = period.count() - 1

  Row(Modifier, spacedBy(8.dp), CenterVertically) {
    Icon(TablerIcons.ChartPie, null)
    Text(pluralResource(amount_uses, uses, uses))

    Icon(TablerIcons.CalendarStats, null)
    Text(pluralResource(amount_days, amountDays, amountDays))
  }
}

@Composable
private fun AverageList(uses: List<Use>, period: LocalDateRange) {
  val totalGrams = uses.totalGrams
  val totalCost = uses.totalCost

  val days = period.count() - 1
  val weeks = days / 7.0
  val months = days / 30.0
  val years = days / 365.25

  AverageListItem(stringResource(day), totalGrams / days.toBigDecimal(), totalCost / days.toBigDecimal())
  if (weeks >= 1.0) {
    AverageListItem(stringResource(week), totalGrams / weeks.toBigDecimal(), totalCost / weeks.toBigDecimal())
  }

  if (months >= 1.0) {
    AverageListItem(stringResource(month), totalGrams / months.toBigDecimal(), totalCost / months.toBigDecimal())
  }

  if (years >= 1.0) {
    AverageListItem(stringResource(year), totalGrams / years.toBigDecimal(), totalCost / years.toBigDecimal())
  }
}

@Composable
private fun AverageListItem(label: String, grams: BigDecimal, cost: BigDecimal) {
  Row {
    Text(stringResource(average_per, label), Modifier.weight(0.5f))
    Text(stringResource(amount_grams, grams), Modifier.weight(0.25f))
    Text(stringResource(currency_symbol) + cost, Modifier.weight(0.25f))
  }
}
