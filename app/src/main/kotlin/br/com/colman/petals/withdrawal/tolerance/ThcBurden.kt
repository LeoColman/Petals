package br.com.colman.petals.withdrawal.tolerance

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit.SECONDS
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.roundToLong

/**
 * Elimination rate of the creatinine-normalized THC metabolite, per day.
 *
 * Derived by log-linear regression over [br.com.colman.petals.withdrawal.data.ThcConcentrationDataPoints],
 * days 0 to 14. The day-20 point is excluded because it is 0.0 and ln(0) is undefined.
 * Half-life is ln(2)/lambda = 4.594 days, which sits inside the 4 to 13 day range reported for
 * THC-COOH terminal elimination in chronic users.
 *
 * Do not fit this over a shorter range: truncating at day 7 yields a 3.0 day half-life, and lambda is
 * load bearing (the steady state reading for a k-fold reduction in intake is exactly ln(k)/lambda).
 *
 * @see br.com.colman.petals.withdrawal.tolerance.ThcDecayConstantTest which re-derives it from the data.
 */
const val ThcEliminationPerDay = 0.150864744989

/**
 * Rate at which a past peak stops counting as the user's tolerance, per day. Half-life of 21 days.
 *
 * This is a tuning constant, not a measured one. It expresses the assumption that tolerance readjusts
 * to a sustained lower intake over a few weeks. Without it, a user who permanently cut their intake
 * eightfold would read ln(8)/lambda = 13.8 days into withdrawal forever, and an isolated binge would
 * keep a stable user flagged for as long as it stayed inside the lookback window.
 */
const val ToleranceReleasePerDay = 0.033

/**
 * The longest abstinence the model will ever claim, in days. Above the widest chart (25 days), and it
 * doubles as the clamp that keeps `-ln(ratio)` finite when the current burden rounds to nothing.
 */
const val MaxEffectiveAbstinenceDays = 30.0

private const val SecondsPerDay = 86_400.0

/** A single logged intake: [amount] is grams in grams mode, or 1.0 per session in sessions mode. */
data class Dose(val at: LocalDateTime, val amount: Double)

/**
 * Body burden at [instant]: the superposition of every dose decaying at [lambda].
 * Doses after [instant] are ignored, so a future-dated entry can never produce e^(+lambda*t).
 */
fun burdenAt(doses: List<Dose>, instant: LocalDateTime, lambda: Double = ThcEliminationPerDay): Double =
  doses.filter { !it.at.isAfter(instant) }
    .sumOf { it.amount * exp(-lambda * daysBetween(it.at, instant)) }

/**
 * The highest burden the user reached, with older peaks released at [tolerance].
 *
 * The release is measured from the most recent dose rather than from now, which is what keeps the model
 * exact for someone who simply stopped: their last dose is its own reference and is released by nothing,
 * so the peak cancels and the result is precisely the elapsed time since it. See [effectiveAbstinence].
 */
fun referenceBurden(
  doses: List<Dose>,
  lambda: Double = ThcEliminationPerDay,
  tolerance: Double = ToleranceReleasePerDay
): Double {
  val ordered = doses.sortedBy { it.at }
  if (ordered.isEmpty()) return 0.0

  val lastDose = ordered.last().at
  val burdens = burdenAtEachDose(ordered, lambda)
  return ordered.indices.maxOf { index ->
    burdens[index] * exp(-tolerance * daysBetween(ordered[index].at, lastDose))
  }
}

/**
 * How long ago a user with this history would have had today's body burden, had they simply stopped.
 *
 * This is the number the withdrawal curves are read at. For someone who quit outright it equals the time
 * since their last use, exactly, whatever their dosing cadence was. For someone who cut down it lags real
 * time, which is the point: an ounce a day dropped to an eighth is not "day zero" of withdrawal.
 *
 * Returns null when the model has nothing to work with (no usable doses), so the caller can fall back.
 */
fun effectiveAbstinence(
  doses: List<Dose>,
  now: LocalDateTime,
  lambda: Double = ThcEliminationPerDay,
  tolerance: Double = ToleranceReleasePerDay
): Duration? {
  val usable = doses.filter { !it.at.isAfter(now) && it.amount.isFinite() && it.amount > 0.0 }
  val reference = referenceBurden(usable, lambda, tolerance)
  val current = burdenAt(usable, now, lambda)
  // Also covers an empty list, whose reference burden is zero.
  if (!reference.isFinite() || reference <= 0.0 || !current.isFinite()) return null

  val minimumRatio = exp(-lambda * MaxEffectiveAbstinenceDays)
  val ratio = (current / reference).coerceIn(minimumRatio, 1.0)
  return (-ln(ratio) / lambda).daysAsDuration()
}

/**
 * Burden evaluated at each dose instant, computed as a running recurrence rather than one sum per dose,
 * so that scanning for the peak stays linear in the number of doses.
 */
private fun burdenAtEachDose(ordered: List<Dose>, lambda: Double): DoubleArray {
  val burdens = DoubleArray(ordered.size)
  var running = 0.0
  for (index in ordered.indices) {
    if (index > 0) running *= exp(-lambda * daysBetween(ordered[index - 1].at, ordered[index].at))
    running += ordered[index].amount
    burdens[index] = running
  }
  return burdens
}

private fun daysBetween(from: LocalDateTime, to: LocalDateTime) = SECONDS.between(from, to) / SecondsPerDay

private fun Double.daysAsDuration() = Duration.ofSeconds((this * SecondsPerDay).roundToLong())
