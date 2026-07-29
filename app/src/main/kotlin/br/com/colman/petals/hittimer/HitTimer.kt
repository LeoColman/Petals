package br.com.colman.petals.hittimer

import android.os.Parcelable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.time.DurationFormatUtils
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit.MILLIS
import java.util.Locale
import java.util.UUID

@Parcelize
class HitTimer(val durationMillis: Long = 10_000L) : Parcelable {

  @IgnoredOnParcel
  private var startDate: LocalDateTime? = null

  /** Time banked by previous runs, so pausing can freeze a reading without discarding it. */
  @IgnoredOnParcel
  private var accumulatedMillis: Long = 0

  /**
   * Identifies the current hit. Stable across pause and resume so that recording it repeatedly updates
   * one row rather than logging the same hit several times; a new one is minted by [start].
   */
  @IgnoredOnParcel
  var runId: String = UUID.randomUUID().toString()
    private set

  /** The reading as it stands, for callers that need it at the instant of a button press. */
  fun elapsedMillis(): Long = calculateMillisElapsed()

  /**
   * How long the current hit has been held. Unlike [millisLeft] this keeps counting past
   * [durationMillis], because the point of the ten second target is that going over it costs you,
   * and you cannot see that you went over if the timer stops at the target.
   */
  @IgnoredOnParcel
  val millisElapsed = flow {
    while (true) {
      emit(calculateMillisElapsed())
      delay(100)
    }
  }

  @IgnoredOnParcel
  val millisLeft = millisElapsed.map { (durationMillis - it).coerceAtLeast(0) }

  fun start() {
    runId = UUID.randomUUID().toString()
    accumulatedMillis = 0
    startDate = now()
  }

  /** Freezes the reading where it is. Without this the elapsed hold would climb until [reset] wiped it. */
  fun pause() {
    accumulatedMillis = calculateMillisElapsed()
    startDate = null
  }

  fun resume() {
    if (startDate == null) startDate = now()
  }

  fun reset() {
    accumulatedMillis = 0
    startDate = null
  }

  private fun calculateMillisElapsed(): Long {
    val start = startDate ?: return accumulatedMillis
    return accumulatedMillis + start.until(now(), MILLIS).coerceAtLeast(0)
  }

  companion object {
    fun formatDuration(millis: Long): String = DurationFormatUtils.formatDuration(millis, "ss:SSS")
    fun formatDurationShort(millis: Long): String = when {
      millis >= 1_000 -> (millis / 1_000).toString()
      millis > 0 -> "%.1f".format(Locale.US, millis / 1_000.0)
      else -> "0.0"
    }
  }
}
