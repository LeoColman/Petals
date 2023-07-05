package br.com.colman.petals.statistics.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.plurals.last_x_days
import br.com.colman.petals.R.string.custom
import br.com.colman.petals.R.string.today
import me.moallemi.tools.daterange.localdate.LocalDateRange
import java.time.LocalDate

sealed class Period(val days: Int?) : Comparable<Period> {
  @Composable
  fun label(): String {
    return when (days) {
      null -> stringResource(custom)
      0 -> stringResource(today)
      else -> pluralStringResource(last_x_days, days, days)
    }
  }

  fun toDateRange(end: LocalDate = LocalDate.now()) = LocalDateRange(end.minusDays(days?.toLong() ?: 0), end)

  override fun compareTo(other: Period): Int = compareValues(days, other.days)

  object Zero : Period(0)
  object Week : Period(7)
  object TwoWeek : Period(14)
  object Month : Period(30)
  object TwoMonth : Period(60)
  object ThreeMonth : Period(90)

  companion object {
    fun values(): Array<Period> {
      return arrayOf(Zero, Week, TwoWeek, Month, TwoMonth, ThreeMonth)
    }
  }
}
