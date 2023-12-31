package br.com.colman.petals.statistics.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.plurals.last_x_days
import br.com.colman.petals.R.string.today
import java.time.LocalDate
import me.moallemi.tools.daterange.localdate.LocalDateRange

sealed class Period(val days: Int) : Comparable<Period> {
  @Composable
  fun label(): String {
    return when (days) {
      0 -> stringResource(today)
      else -> pluralStringResource(last_x_days, days, days)
    }
  }

  fun toDateRange(end: LocalDate = LocalDate.now()) = LocalDateRange(end.minusDays(days.toLong()), end)

  override fun compareTo(other: Period): Int = compareValues(days, other.days)

  data object Zero : Period(0)
  data object Week : Period(7)
  data object TwoWeek : Period(14)
  data object Month : Period(30)
  data object TwoMonth : Period(60)
  data object ThreeMonth : Period(90)

  companion object {
    fun values(): Array<Period> {
      return arrayOf(Zero, Week, TwoWeek, Month, TwoMonth, ThreeMonth)
    }
  }
}
