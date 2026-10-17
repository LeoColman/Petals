package br.com.colman.petals.statistics.graph.data

import java.time.LocalTime

private const val MinutesInHour = 60
private const val MinutesInDay = 24 * MinutesInHour

/**
 * How finely the per-hour graph splits the day. Either way the x axis stays in hours, so 20:15 sits
 * at 20.25 and the hour labels and break bands stay where they are.
 *
 * [drawsDots] is whether each reading gets a dot. Ninety-six dots a few dp apart run together into a
 * second, thicker line, so the quarter-hour line goes without them.
 */
enum class TimeOfDayResolution(private val minutes: Int, val drawsDots: Boolean) {
  Hour(60, true),
  QuarterHour(15, false);

  val bucketCount get() = MinutesInDay / minutes

  /** The bucket [time] falls in, counting from midnight. */
  fun bucketOf(time: LocalTime) = (time.hour * MinutesInHour + time.minute) / minutes

  /** Where [bucket] starts, in hours. */
  fun hourOf(bucket: Int) = bucket * minutes / MinutesInHour.toFloat()

  /** Where the last bucket starts, which is where the axis ends: 23 by the hour, 23.75 by the quarter. */
  val lastHour get() = hourOf(bucketCount - 1)

  companion object {
    /** The resolution a switch stands for: off is by the hour, on is by the quarter hour. */
    fun of(isQuarterHour: Boolean) = if (isQuarterHour) QuarterHour else Hour
  }
}
