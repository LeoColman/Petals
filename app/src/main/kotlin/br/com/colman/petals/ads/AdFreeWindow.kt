package br.com.colman.petals.ads

import java.time.Duration
import java.time.Instant

/**
 * How long a single rewarded video buys. Deliberately short: long enough to be worth watching,
 * short enough that people come back for another one instead of never seeing an ad again.
 */
val RewardedAdFreeDuration: Duration = Duration.ofHours(24)

/**
 * Slack for the device clock drifting slightly forward between the grant and the check.
 */
private val ClockSkewTolerance: Duration = Duration.ofMinutes(5)

/**
 * Time left in the ad-free window ending at [until], or null when there is none.
 *
 * A window sitting further ahead than [RewardedAdFreeDuration] can only come from the device clock
 * being moved backwards, so it counts as expired instead of as free time.
 */
fun adFreeTimeRemaining(until: Instant?, now: Instant): Duration? {
  if (until == null || !now.isBefore(until)) return null

  return Duration.between(now, until).takeIf { it <= RewardedAdFreeDuration + ClockSkewTolerance }
}
