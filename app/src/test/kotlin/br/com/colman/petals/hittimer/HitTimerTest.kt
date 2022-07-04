package br.com.colman.petals.hittimer

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldEndWith
import io.kotest.matchers.collections.shouldStartWith
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
    allResults shouldStartWith 100
    allResults shouldEndWith 0
  }

  test("Should emit at least 10% of values in duration") {
    val flow = target.millisLeft
    target.start()
    val allResults = flow.take(300).toList()
    allResults.size shouldBeGreaterThanOrEqual (duration / 10).toInt()
  }
})
