package br.com.colman.petals.hittimer

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeMonotonicallyDecreasing
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile
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

  context("Error Cases") {
    test("Should return max duration if timer wasn't started") {
      target.millisLeft.first() shouldBe duration
    }
  }

  context("Reset functionality") {
    test("Should reset to initial duration when reset is called after starting") {
      target.start()
      target.reset()
      target.millisLeft.first() shouldBe duration
    }

    test("After timer completes, reset should restore duration") {
      target.start()
      target.millisLeft.takeWhile { it > 0 }.toList()
      target.reset()
      target.millisLeft.first() shouldBe duration
    }
  }

  context("Start multiple times") {
    test("Calling start multiple times resets the timer") {
      target.start()
      delay(20L)
      val firstValue = target.millisLeft.first()
      target.start()
      val secondValue = target.millisLeft.first()

      secondValue shouldBe duration
      firstValue shouldBeLessThanOrEqual (duration - 20)
    }
  }

  context("Duration formatting") {
    withData(
      nameFn = { (millis, expected) -> "$millis ms -> $expected" },
      0L to "00:000",
      1000L to "01:000",
      1234L to "01:234",
      9999L to "09:999",
      10_000L to "10:000"
    ) { (millis, expected) ->
      HitTimer.formatDuration(millis) shouldBe expected
    }
  }

  context("Duration with milliseconds disabled edge cases") {
    withData(
      nameFn = { (millis, string) -> "$millis ms -> $string" },
      999L to "1.0",
      1000L to "1",
      1001L to "1",
      500L to "0.5",
      499L to "0.5",
      1L to "0.0",
      0L to "0.0"
    ) { (millis, string) ->
      HitTimer.formatDurationShort(millis) shouldBe string
    }
  }

  test("Timer stays at zero after duration has passed") {
    target.start()
    delay(duration)
    target.millisLeft.first() shouldBe 0
    target.millisLeft.take(3).toList().forAll { it shouldBe 0 }
  }
})
