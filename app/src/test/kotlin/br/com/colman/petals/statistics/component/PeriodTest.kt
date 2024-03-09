package br.com.colman.petals.statistics.component

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import me.moallemi.tools.daterange.localdate.LocalDateRange
import java.time.LocalDate

class PeriodTest : FunSpec({
  val now = LocalDate.now()
  val week = Period.Week
  val twoWeek = Period.TwoWeek

  test("toDateRange should return correct range") {
    val expectedWeekRange = LocalDateRange(now.minusDays(6), now)
    week.toDateRange(now) shouldBeEqualToComparingFields expectedWeekRange
    week.toDateRange(now).count() shouldBe 7

    val expectedTwoWeekRange = LocalDateRange(now.minusDays(13), now)
    twoWeek.toDateRange(now) shouldBeEqualToComparingFields expectedTwoWeekRange
    twoWeek.toDateRange(now).count() shouldBe 14
  }

  test("compareTo should return correct comparison") {
    week shouldBeLessThan twoWeek
    twoWeek shouldBeGreaterThan week
    week shouldBeEqualComparingTo week
  }
})
