package br.com.colman.petals.hittimer

import android.os.Parcelable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
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

  @IgnoredOnParcel
  val millisLeft = flow {
    while (true) {
      emit(calculateMillisLeft())
      delay(100)
    }
  }

  fun start() {
    startDate = now()
  }

  fun reset() {
    startDate = null
  }

  private fun calculateMillisLeft(): Long {
    if (startDate == null) return durationMillis
    val elapsed = startDate?.until(now(), MILLIS) ?: 0
    return (durationMillis - elapsed).coerceAtLeast(0)
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
