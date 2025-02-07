package br.com.colman.petals.withdrawal.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration.ofDays

class DiscomfortTest : FunSpec({

  val target = DiscomfortDataPoints

  test("Should match the snapshot") {
    val snapshot = mapOf(
      ofDays(-1) to 3.5,
      ofDays(0) to 3.5,
      ofDays(2) to 7.5,
      ofDays(5) to 7.3,
      ofDays(8) to 6.6,
      ofDays(11) to 5.3,
      ofDays(14) to 5.0,
      ofDays(17) to 5.0,
      ofDays(20) to 4.0,
      ofDays(23) to 3.5,
      ofDays(25) to 3.0,
    )

    target shouldBe snapshot
  }
})
