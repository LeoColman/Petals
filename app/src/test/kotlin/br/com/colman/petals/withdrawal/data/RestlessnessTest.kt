package br.com.colman.petals.withdrawal.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration.ofDays

class RestlessnessTest : FunSpec({

  val target = RestlessnessDataPoints

  test("Should match the snapshot") {
    val snapshot = mapOf(
      ofDays(-1) to 0.43,
      ofDays(0) to 0.43,
      ofDays(2) to 0.71,
      ofDays(5) to 1.06,
      ofDays(8) to 0.81,
      ofDays(11) to 0.53,
      ofDays(14) to 0.45,
      ofDays(17) to 0.33,
      ofDays(20) to 0.29,
      ofDays(23) to 0.32,
      ofDays(25) to 0.17,
    )

    target shouldBe snapshot
  }
})
