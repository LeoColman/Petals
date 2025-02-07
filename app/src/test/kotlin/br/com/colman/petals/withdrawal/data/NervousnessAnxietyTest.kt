package br.com.colman.petals.withdrawal.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration.ofDays

class NervousnessAnxietyTest : FunSpec({

  val target = NervousnessAnxietyDataPoints

  test("Should match the snapshot") {
    val snapshot = mapOf(
      ofDays(-1) to 0.43,
      ofDays(0) to 0.43,
      ofDays(2) to 0.60,
      ofDays(5) to 0.61,
      ofDays(8) to 0.54,
      ofDays(11) to 0.46,
      ofDays(14) to 0.39,
      ofDays(17) to 0.37,
      ofDays(20) to 0.21,
      ofDays(23) to 0.25,
      ofDays(25) to 0.25,
    )

    target shouldBe snapshot
  }
})
