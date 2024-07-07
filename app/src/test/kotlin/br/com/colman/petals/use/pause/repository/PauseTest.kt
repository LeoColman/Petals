package br.com.colman.petals.use.pause.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.localTime
import io.kotest.property.checkAll
import java.time.LocalTime
import java.time.LocalTime.MAX
import java.time.LocalTime.MIN
import java.time.LocalTime.NOON

class PauseTest : FunSpec({
  context("Is active calculation") {
    test("Full time pause") {
      val pause = Pause(startTime = MIN, endTime = MAX)

      Arb.localTime().checkAll { pause shouldBeActiveAt it }
    }

    test("Immediately after end") {
      val pause = Pause(startTime = MIN, endTime = NOON)

      pause shouldNotBeActiveAt NOON.plusSeconds(1)
    }

    test("Immediately before start") {
      val pause = Pause(startTime = NOON, endTime = MAX)
      pause shouldNotBeActiveAt NOON.minusSeconds(1)
    }

    test("Pause disabled") {
      val pause = Pause(startTime = MIN, endTime = MAX, isEnabled = false)
      pause shouldNotBeActiveAt NOON
    }

    context("Passing through midnight") {
      val pause = Pause(startTime = MAX, endTime = NOON)

      test("Start time") {
        pause shouldBeActiveAt MAX.plusSeconds(1)
        pause shouldNotBeActiveAt MAX.minusSeconds(1)
      }

      test("End time") {
        pause shouldBeActiveAt NOON.minusSeconds(1)
        pause shouldNotBeActiveAt NOON.plusSeconds(1)
      }
    }
  }
})

private infix fun Pause.shouldBeActiveAt(localTime: LocalTime) {
  isActive(localTime) shouldBe true
}

private infix fun Pause.shouldNotBeActiveAt(localTime: LocalTime) {
  isActive(localTime) shouldBe false
}
