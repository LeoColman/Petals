package br.com.colman.petals.use.pause.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.localTime
import io.kotest.property.checkAll
import java.time.LocalTime
import java.time.LocalTime.NOON

class PauseTest : FunSpec({

  context("Is active calculation") {

    test("Full time pause") {
      val pause = Pause(LocalTime.MIN, LocalTime.MAX)

      Arb.localTime().checkAll {
        pause.isActive(it) shouldBe true
      }
    }

    test("Immediately after end") {
      val pause = Pause(LocalTime.MIN, NOON)

      pause.isActive(NOON.plusSeconds(1)) shouldBe false
    }

    test("Immediately before start") {
      val pause = Pause(LocalTime.NOON, LocalTime.MAX)
      pause.isActive(NOON.minusSeconds(1)) shouldBe false
    }

    test("Goes through midnight") {
      val pause = Pause(LocalTime.of(23, 30), LocalTime.of(1, 30))
      pause.isActive(LocalTime.of(0, 25)) shouldBe true
      pause.isActive(LocalTime.of(1, 31)) shouldBe false
      pause.isActive(LocalTime.of(23, 29)) shouldBe false
    }
  }
})
