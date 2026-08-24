package br.com.colman.petals.statistics.graph.formatter

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ShortDayOfWeek = DateTimeFormatter.ofPattern("E")

/** Weekday axis labels, abbreviated and localised: 1 is Monday. */
val DayOfWeekFormatter: (Float) -> String = { value ->
  val dayOfWeek = DayOfWeek.of(value.toInt())
  LocalDate.now().with(dayOfWeek)
    .format(ShortDayOfWeek.withLocale(Locale.getDefault()))
    .replace(".", "")
}
