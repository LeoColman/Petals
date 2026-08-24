package br.com.colman.petals.statistics.graph.formatter

import kotlin.math.roundToInt

/** Hour-of-day axis labels on a twelve hour clock, so 13 reads as 1 while noon stays 12 rather than 0. */
@Suppress("MagicNumber")
val TwelveHourFormatter: (Float) -> String = { value ->
  if (value.roundToInt() == 12) "12" else (value.roundToInt() % 12).toString()
}
