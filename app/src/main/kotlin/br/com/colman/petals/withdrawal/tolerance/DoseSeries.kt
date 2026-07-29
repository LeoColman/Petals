package br.com.colman.petals.withdrawal.tolerance

import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.withdrawal.tolerance.EstimateMode.Disabled
import br.com.colman.petals.withdrawal.tolerance.EstimateMode.Grams
import br.com.colman.petals.withdrawal.tolerance.EstimateMode.LastUseOnly
import br.com.colman.petals.withdrawal.tolerance.EstimateMode.Sessions
import java.time.Duration
import java.time.LocalDateTime

/** How far back uses are read. Only a data volume bound: the tolerance release already fades old peaks. */
const val ToleranceLookbackDays = 90L

/** Below this many logged uses in the window there is nothing to compare a reduction against. */
const val MinDosesForModel = 3

/** Fraction of the window's uses that must carry an amount before amounts are trusted as doses. */
const val GramsCoverageThreshold = 0.80

/**
 * Turns the raw use history into the number the withdrawal curves are read at.
 *
 * Three tiers, in order:
 * - **Grams** when the window has enough uses and most of them record an amount.
 * - **Sessions** when amounts are mostly missing. Burden is proportional to how often the user logs, so
 *   eight sessions a day dropping to one still reads as ln(8)/lambda, exactly as eight grams to one would.
 * - **LastUseOnly** otherwise, and **Disabled** when the user switched the adjustment off: both read the
 *   literal time since the last use, which is what the app did before tolerance was modelled.
 *
 * Returns null when there is no use at all to anchor on.
 */
fun estimateAbstinence(
  uses: List<Use>,
  lastUseDate: LocalDateTime?,
  now: LocalDateTime,
  modelEnabled: Boolean
): AbstinenceEstimate? {
  val actual = lastUseDate?.elapsedUntil(now) ?: return null

  val modelled = if (modelEnabled) modelledAbstinence(uses, now) else null
  val fallback = if (modelEnabled) LastUseOnly else Disabled
  return modelled?.let { (effective, mode) -> AbstinenceEstimate(effective, actual, mode) }
    ?: AbstinenceEstimate(actual, actual, fallback)
}

/** The tolerance-adjusted reading and the currency it was computed in, or null when it cannot be run. */
private fun modelledAbstinence(uses: List<Use>, now: LocalDateTime): Pair<Duration, EstimateMode>? {
  val window = uses.filter { !it.date.isAfter(now) && !it.date.isBefore(now.minusDays(ToleranceLookbackDays)) }
  if (window.size < MinDosesForModel) return null

  val amounts = window.map { it.sanitizedAmount() }
  val coverage = amounts.count { it > 0.0 }.toDouble() / amounts.size

  val mode = if (coverage >= GramsCoverageThreshold) Grams else Sessions
  val doses = when (mode) {
    Grams -> window.zip(amounts).filter { (_, amount) -> amount > 0.0 }.map { (use, amount) -> Dose(use.date, amount) }
    else -> window.map { Dose(it.date, 1.0) }
  }

  return effectiveAbstinence(doses, now)?.let { it to mode }
}

/**
 * The logged amount as a usable dose, taken at face value, or 0.0 when it is missing, negative or not a
 * finite number. An empty amount field is stored as zero rather than rejected, so that is a common shape
 * here, not an edge case.
 */
private fun Use.sanitizedAmount(): Double {
  val grams = amountGrams.toDouble()
  return if (grams.isFinite() && grams > 0.0) grams else 0.0
}

/** Time from this instant until [now], or zero if this is in the future. */
private fun LocalDateTime.elapsedUntil(now: LocalDateTime): Duration =
  if (isAfter(now)) Duration.ZERO else Duration.between(this, now)
