package br.com.colman.petals.withdrawal.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration.ofDays

class SleepDifficultyTest : FunSpec({

  val target = SleepDifficultyDataPoints

  test("Should match the snapshot") {
    val snapshot = mapOf(
      ofDays(-1) to 0.5,
      ofDays(0) to 0.5,
      ofDays(2) to 1.0,
      ofDays(5) to 0.87,
      ofDays(8) to 0.90,
      ofDays(11) to 0.53,
      ofDays(14) to 0.56,
      ofDays(17) to 0.97,
      ofDays(20) to 0.85,
      ofDays(23) to 0.53,
      ofDays(25) to 0.53,
    )

    target shouldBe snapshot
  }
})
