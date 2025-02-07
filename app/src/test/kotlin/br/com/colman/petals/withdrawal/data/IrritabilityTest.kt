package br.com.colman.petals.withdrawal.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration.ofDays

class IrritabilityTest : FunSpec({

  val target = IrritabilityDataPoints

  test("Should match the snapshot") {
    val snapshot = mapOf(
      ofDays(-1) to 0.5,
      ofDays(0) to 0.5,
      ofDays(2) to 0.9,
      ofDays(5) to 1.0,
      ofDays(8) to 0.85,
      ofDays(11) to 0.75,
      ofDays(14) to 0.70,
      ofDays(17) to 0.66,
      ofDays(20) to 0.52,
      ofDays(23) to 0.32,
      ofDays(25) to 0.26,
    )

    target shouldBe snapshot
  }
})
