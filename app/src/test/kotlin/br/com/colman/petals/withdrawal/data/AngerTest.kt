package br.com.colman.petals.withdrawal.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration.ofDays

class AngerTest : FunSpec({

  val target = AngerDataPoints

  test("Should match the snapshot") {
    val snapshot = mapOf(
      ofDays(-1) to 0.16,
      ofDays(0) to 0.16,
      ofDays(2) to 0.35,
      ofDays(5) to 0.38,
      ofDays(8) to 0.39,
      ofDays(11) to 0.40,
      ofDays(14) to 0.36,
      ofDays(17) to 0.38,
      ofDays(20) to 0.20,
      ofDays(23) to 0.17,
      ofDays(25) to 0.09,
    )

    target shouldBe snapshot
  }
})
