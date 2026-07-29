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

@Parcelize
class HitTimer(val durationMillis: Long = 10_000L) : Parcelable {

  @IgnoredOnParcel
  private var startDate: LocalDateTime? = null

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
    startDate = now()
  }

  fun reset() {
    startDate = null
  }

  private fun calculateMillisElapsed(): Long {
    val start = startDate ?: return 0
    return start.until(now(), MILLIS).coerceAtLeast(0)
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
