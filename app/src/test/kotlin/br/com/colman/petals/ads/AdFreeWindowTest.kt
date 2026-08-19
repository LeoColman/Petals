package br.com.colman.petals.ads

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.time.Duration
import java.time.Instant

private val Now: Instant = Instant.parse("2026-01-01T12:00:00Z")

class AdFreeWindowTest : FunSpec({

  test("Without a granted window there is no ad-free time") {
    adFreeTimeRemaining(null, Now).shouldBeNull()
  }

  test("A window still ahead reports how much is left") {
    val until = Now.plus(Duration.ofHours(5))

    adFreeTimeRemaining(until, Now) shouldBe Duration.ofHours(5)
  }

  test("A window that already ended is over") {
    adFreeTimeRemaining(Now.minusSeconds(1), Now).shouldBeNull()
  }

  test("A window ending exactly now is over") {
    adFreeTimeRemaining(Now, Now).shouldBeNull()
  }

  test("A freshly granted window lasts the full reward duration") {
    val until = Now.plus(RewardedAdFreeDuration)

    adFreeTimeRemaining(until, Now) shouldBe RewardedAdFreeDuration
  }
})
