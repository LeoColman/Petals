package br.com.colman.petals.hittimer

import br.com.colman.petals.hittimer.repository.Hit
import java.time.Duration
import java.time.LocalDateTime

/** Windows the hold average is reported over, longest first. */
enum class HoldWindow(val days: Long?) {
  AllTime(null),
  Last30Days(30),
  Last7Days(7)
}

/**
 * @param average null when the window holds no hits, which reads differently from an average of zero
 */
data class HoldAverage(val window: HoldWindow, val average: Duration?, val count: Int)

/**
 * Mean hold per window. Pure, so it is unit-testable, and the only place the windows are defined.
 *
 * Hits after [now] are ignored rather than trusted: the date picker elsewhere in the app allows future
 * dates, and a clock that moved backwards would otherwise drag the average.
 */
fun holdAverages(hits: List<Hit>, now: LocalDateTime): List<HoldAverage> =
  HoldWindow.entries.map { window ->
    val cutoff = window.days?.let { now.minusDays(it) }
    val inWindow = hits.filter { !it.date.isAfter(now) && (cutoff == null || !it.date.isBefore(cutoff)) }

    HoldAverage(window, inWindow.meanDuration(), inWindow.size)
  }

private fun List<Hit>.meanDuration(): Duration? {
  if (isEmpty()) return null
  return Duration.ofMillis(sumOf { it.duration.toMillis() } / size)
}
