package br.com.colman.petals.use.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.view.calendar.UseDay
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import timber.log.Timber
import java.time.DayOfWeek.*
import java.time.LocalDate
import java.time.LocalDateTime

fun fodaceUse(date: LocalDateTime) = Use(date, 1.0.toBigDecimal(), 3.toBigDecimal())
fun prensadoUse(date: LocalDateTime) = Use(date, 0.30.toBigDecimal(), 5.toBigDecimal())
fun florzinhaUse(date: LocalDateTime) = Use(date, 0.15.toBigDecimal(), 50.toBigDecimal())

@Composable
@Preview
private fun SingleUseStaticCalendarPreview() {
  val today = LocalDate.now()
  val uses = listOf(prensadoUse(today.atStartOfDay()))
  StaticUseCalendar(listOf(today), uses)
}

@Composable
@Preview
private fun DailyUseForLastWeekCalendarPreview() {
  val lastWeek = List(7) { LocalDate.now().minusDays(it.toLong()) }
  val uses = lastWeek.mapNotNull {
    if (it.dayOfWeek == WEDNESDAY) return@mapNotNull null

    if (it.dayOfWeek in listOf(FRIDAY, SATURDAY)) {
      florzinhaUse(it.atStartOfDay())
    } else if (it.dayOfWeek == SUNDAY) {
      fodaceUse(it.atStartOfDay())
    } else {
      prensadoUse(it.atStartOfDay())
    }
  }
  Timber.d("$lastWeek $uses")
  StaticUseCalendar(lastWeek, uses)
}

@Composable
fun StaticUseCalendar(
  datesToSelect: List<LocalDate>,
  uses: List<Use>
) {
  val calendarState = rememberSelectableCalendarState(
    initialSelection = datesToSelect,
    initialSelectionMode = SelectionMode.Multiple
  )

  SelectableCalendar(calendarState = calendarState, dayContent = { state ->
    UseDay(state.date, datesToSelect, uses)
//    DefaultDay(state)
  })
}
