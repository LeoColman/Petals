package br.com.colman.petals.ads

import java.time.Duration
import java.time.Instant

/**
 * How long a single rewarded video buys. Deliberately short: long enough to be worth watching,
 * short enough that people come back for another one instead of never seeing an ad again.
 */
val RewardedAdFreeDuration: Duration = Duration.ofHours(24)

/**
 * Time left in the ad-free window ending at [until], or null when there is none.
 */
fun adFreeTimeRemaining(until: Instant?, now: Instant): Duration? {
  if (until == null || !until.isAfter(now)) return null

  return Duration.between(now, until)
}
