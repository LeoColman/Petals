package br.com.colman.petals.withdrawal.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration.ofDays

class DecreasedAppetiteTest : FunSpec({

  val target = DecreasedAppetiteDataPoints

  test("Should match the snapshot") {
    val snapshot = mapOf(
      ofDays(-1) to 0.32,
      ofDays(0) to 0.32,
      ofDays(2) to 0.89,
      ofDays(5) to 0.65,
      ofDays(8) to 0.39,
      ofDays(11) to 0.36,
      ofDays(14) to 0.29,
      ofDays(17) to 0.20,
      ofDays(20) to 0.19,
      ofDays(23) to 0.22,
      ofDays(25) to 0.16,
    )

    target shouldBe snapshot
  }
})
