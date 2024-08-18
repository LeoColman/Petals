
package br.com.colman.petals.statistics.card

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.plurals.amount_days
import br.com.colman.petals.R.plurals.amount_uses
import br.com.colman.petals.R.string.amount_grams
import br.com.colman.petals.R.string.amount_grams_short
import br.com.colman.petals.R.string.average_per
import br.com.colman.petals.R.string.day
import br.com.colman.petals.R.string.month
import br.com.colman.petals.R.string.week
import br.com.colman.petals.R.string.year
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalCost
import br.com.colman.petals.use.repository.totalGrams
import br.com.colman.petals.utils.pluralResource
import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarStats
import compose.icons.tablericons.ChartPie
import compose.icons.tablericons.Scale
import me.moallemi.tools.daterange.localdate.LocalDateRange
import me.moallemi.tools.daterange.localdate.rangeTo
import org.koin.compose.koinInject
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
fun AverageUseCard(uses: List<Use>, period: LocalDateRange) {
  if (uses.isEmpty()) return

  Card(Modifier.padding(8.dp).fillMaxWidth().horizontalScroll(rememberScrollState()).width(Max)) {
    Column(Modifier.padding(8.dp).fillMaxWidth().width(Max), spacedBy(8.dp)) {
      AverageUseCardTitle(uses.count(), uses.totalGrams, period)
      AverageUseList(uses, period)
    }
  }
}

@Preview
@Composable
private fun AverageUseCardTitlePreview() {
  val now = now()
  Column {
    AverageUseCardTitle(1, 1.toBigDecimal(), now..now)
    AverageUseCardTitle(5, 5.toBigDecimal(), now.minusDays(1L)..now)
    AverageUseCardTitle(32, 32.25.toBigDecimal(), now.minusDays(3L)..now)
  }
}

@Composable
private fun AverageUseCardTitle(uses: Int, grams: BigDecimal, period: LocalDateRange) {
  val amountDays = period.count()

  Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp), CenterVertically) {
    IconText(TablerIcons.ChartPie, pluralResource(amount_uses, uses, uses))
    IconText(TablerIcons.Scale, stringResource(amount_grams, "%.2f".format(grams)))
    IconText(TablerIcons.CalendarStats, pluralResource(amount_days, amountDays, amountDays))
  }
}

@Preview
@Composable
private fun IconTextPreview() {
  IconText(TablerIcons.ChartPie, "25 uses")
}

@Composable
private fun IconText(icon: ImageVector, text: String) {
  Row(Modifier, spacedBy(8.dp), CenterVertically) {
    Icon(icon, text)
    Text(text)
  }
}

@Composable
private fun AverageUseList(uses: List<Use>, period: LocalDateRange) {
  val totalGrams = uses.totalGrams.toDouble()
  val totalCost = uses.totalCost.toDouble()

  val days = period.count().toDouble()
  val weeks = days / 7.0
  val months = days / 30.0
  val years = days / 365.25

  @Composable
  fun AverageListItem(label: String, value: Double) {
    if (value >= 1.0) {
      AverageListItem(label, totalGrams / value, totalCost / value, uses.size / value)
    }
  }

  AverageListItem(stringResource(day), days)
  AverageListItem(stringResource(week), weeks)
  AverageListItem(stringResource(month), months)
  AverageListItem(stringResource(year), years)
}

@Composable
private fun AverageListItem(label: String, grams: Double, cost: Double, uses: Double) {
  Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
    Text(stringResource(average_per, label), Modifier.padding(2.dp))
    Text(stringResource(amount_grams_short, "%.2f".format(grams)), Modifier.padding(2.dp))
    Text(getCurrencyIcon() + "%.2f".format(cost), Modifier.padding(2.dp))
    Text(pluralResource(amount_uses, uses.toInt(), uses.toInt()), Modifier.padding(2.dp))
  }
}

@Composable
private fun getCurrencyIcon(): String {
  val settingsRepository = koinInject<SettingsRepository>()
  val currencyIcon by settingsRepository.currencyIcon.collectAsState("$")
  return currencyIcon
}
