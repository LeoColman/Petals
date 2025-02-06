package br.com.colman.petals.withdrawal.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeMonotonicallyDecreasing
import io.kotest.matchers.shouldBe
import java.time.Duration.ofDays

class ThcConcentrationTest : FunSpec({

  val target = ThcConcentrationDataPoints

  test("Should match the snapshot") {
    val snapshot = mapOf(
      ofDays(0) to 250.0,
      ofDays(1) to 175.0,
      ofDays(2) to 125.0,
      ofDays(3) to 100.0,
      ofDays(4) to 75.0,
      ofDays(7) to 50.0,
      ofDays(10) to 45.0,
      ofDays(14) to 25.0,
      ofDays(20) to 0.0
    )

    target shouldBe snapshot
  }

  test("Should be monotonically decreasing") {
    target.toSortedMap().values.shouldBeMonotonicallyDecreasing()
  }
})
