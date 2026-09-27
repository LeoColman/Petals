package br.com.colman.petals.drugtest

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class DrugTestPageTest : FunSpec({
  val today = LocalDate.of(2026, 9, 27)

  context("days since the last use") {
    test("is unknown without a last use") {
      computeDaysSince(null, today).shouldBeNull()
    }

    test("counts whole days up to today") {
      computeDaysSince(LocalDate.of(2026, 9, 20), today) shouldBe 7
    }

    test("is zero on the day of the last use") {
      computeDaysSince(today, today) shouldBe 0
    }

    test("is zero, not negative, for a use logged in the future") {
      computeDaysSince(LocalDate.of(2026, 9, 30), today) shouldBe 0
    }
  }

  context("days remaining until the detection window closes") {
    test("is unknown without a last use") {
      computeDaysRemaining(null, 30).shouldBeNull()
    }

    test("is the window minus the days already clean") {
      computeDaysRemaining(7, 30) shouldBe 23
    }

    test("is zero, not negative, once the window has passed") {
      computeDaysRemaining(45, 30) shouldBe 0
    }
  }
})
