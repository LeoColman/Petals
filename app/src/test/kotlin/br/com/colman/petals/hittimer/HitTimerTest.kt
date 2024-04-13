package br.com.colman.petals.hittimer

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeMonotonicallyDecreasing
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList

class HitTimerTest : FunSpec({

  val duration = 100L
  val target = HitTimer(duration)

  test("Should emit the entire duration when not started") {
    target.millisLeft.take(3).toList().forAll {
      it shouldBe duration
    }
  }

  test("Should decrease down to zero after start") {
    val flow = target.millisLeft
    target.start()
    val allResults = flow.take(300).toList()
    allResults.shouldBeMonotonicallyDecreasing()
    allResults.last() shouldBe 0
  }

  test("Should emit at least 10% of values in duration") {
    val flow = target.millisLeft
    target.start()
    val allResults = flow.take(300).toList()
    allResults.size shouldBeGreaterThanOrEqual (duration / 10).toInt()
  }

  context("Duration with milliseconds disabled should not show 0 while there are still milliseconds") {
    withData(
      nameFn = { (millis, string) -> "$millis milliseconds should be converted to $string" },
      0L to "0.0",
      100L to "0.1",
      249L to "0.2",
      250L to "0.3",
      666L to "0.7",
      1200L to "1"
    ) { (millis, string) ->
      HitTimer.durationMillisecondsDisabled(millis) shouldBe string
    }
  }
})
