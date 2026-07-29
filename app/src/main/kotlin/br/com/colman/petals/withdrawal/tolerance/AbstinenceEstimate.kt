package br.com.colman.petals.withdrawal.tolerance

import java.time.Duration

/** Which currency the tolerance model was able to run in, or that it could not run at all. */
enum class EstimateMode {
  /** Logged amounts were used as doses. */
  Grams,

  /** Amounts were mostly missing, so each logged use counted as one session. */
  Sessions,

  /** Not enough history to compare against: the reading is plain time since the last use. */
  LastUseOnly,

  /** The user turned the tolerance adjustment off. Same reading as [LastUseOnly], different reason. */
  Disabled
}

/**
 * @param effective where the withdrawal curves are read, accounting for how much the user has been using
 * @param actual literal time since the last logged use, which the Usage tab also shows
 */
data class AbstinenceEstimate(
  val effective: Duration,
  val actual: Duration,
  val mode: EstimateMode
)
