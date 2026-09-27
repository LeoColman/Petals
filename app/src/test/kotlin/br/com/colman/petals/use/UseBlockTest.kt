package br.com.colman.petals.use

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import java.time.LocalDate

class UseBlockTest : FunSpec({
  context("this week runs from Monday to Sunday") {
    val sunday = LocalDate.of(2026, 9, 27)

    test("the Monday before a Sunday is the same week") {
      (LocalDate.of(2026, 9, 21) isSameWeekAs sunday).shouldBeTrue()
    }

    test("the Monday after a Sunday starts a new week") {
      (LocalDate.of(2026, 9, 28) isSameWeekAs sunday).shouldBeFalse()
    }

    test("a week that spans New Year is still one week") {
      (LocalDate.of(2026, 12, 31) isSameWeekAs LocalDate.of(2027, 1, 1)).shouldBeTrue()
    }
  }

  context("this month") {
    test("the first and last days of a month are the same month") {
      (LocalDate.of(2026, 9, 1) isSameMonthAs LocalDate.of(2026, 9, 30)).shouldBeTrue()
    }

    test("the next day after the end of a month is a new month") {
      (LocalDate.of(2026, 9, 30) isSameMonthAs LocalDate.of(2026, 10, 1)).shouldBeFalse()
    }

    test("the same month a year earlier is a different month") {
      (LocalDate.of(2025, 9, 15) isSameMonthAs LocalDate.of(2026, 9, 15)).shouldBeFalse()
    }
  }
})
