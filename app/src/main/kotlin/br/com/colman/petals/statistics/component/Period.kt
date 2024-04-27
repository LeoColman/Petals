package br.com.colman.petals.statistics.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.plurals.last_x_days
import br.com.colman.petals.R.string.today
import me.moallemi.tools.daterange.localdate.LocalDateRange
import java.time.LocalDate

open class Period(val days: Int) : Comparable<Period> {
  @Composable
  fun label(): String {
    return when (days) {
      0 -> stringResource(today)
      else -> pluralStringResource(last_x_days, days, days)
    }
  }

  fun minusDays(amount: Int) = Period(days - amount)

  fun toDateRange(end: LocalDate = LocalDate.now()): LocalDateRange {
    val start = end.minusDays(days.toLong())
    val startDate = if (start == end) start else start.plusDays(1)
    return LocalDateRange(startDate, end)
  }

  override fun compareTo(other: Period): Int = compareValues(days, other.days)
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Period) return false

    if (days != other.days) return false

    return true
  }

  override fun hashCode(): Int {
    return days
  }


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
