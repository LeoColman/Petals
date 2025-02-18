package br.com.colman.petals.statistics.component

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.moallemi.tools.daterange.localdate.LocalDateRange
import java.time.LocalDate

class PeriodTest : FunSpec({
  val now = LocalDate.now()

  val zero = Period.Zero
  val week = Period.Week
  val twoWeek = Period.TwoWeek
  val month = Period.Month
  val twoMonth = Period.TwoMonth
  val threeMonth = Period.ThreeMonth

  context("toDateRange should return correct range") {
    test("Zero period should return single day range") {
      val expectedZeroRange = LocalDateRange(now, now)
      zero.toDateRange(now) shouldBeEqualToComparingFields expectedZeroRange
      zero.toDateRange(now).count() shouldBe 1
    }

    test("Week period should return 7-day range") {
      val expectedWeekRange = LocalDateRange(now.minusDays(6), now)
      week.toDateRange(now) shouldBeEqualToComparingFields expectedWeekRange
      week.toDateRange(now).count() shouldBe 7
    }

    test("Two-week period should return 14-day range") {
      val expectedTwoWeekRange = LocalDateRange(now.minusDays(13), now)
      twoWeek.toDateRange(now) shouldBeEqualToComparingFields expectedTwoWeekRange
      twoWeek.toDateRange(now).count() shouldBe 14
    }

    test("Month period should return 30-day range") {
      val expectedMonthRange = LocalDateRange(now.minusDays(29), now)
      month.toDateRange(now) shouldBeEqualToComparingFields expectedMonthRange
      month.toDateRange(now).count() shouldBe 30
    }

    test("Two-month period should return 60-day range") {
      val expectedTwoMonthRange = LocalDateRange(now.minusDays(59), now)
      twoMonth.toDateRange(now) shouldBeEqualToComparingFields expectedTwoMonthRange
      twoMonth.toDateRange(now).count() shouldBe 60
    }

    test("Three-month period should return 90-day range") {
      val expectedThreeMonthRange = LocalDateRange(now.minusDays(89), now)
      threeMonth.toDateRange(now) shouldBeEqualToComparingFields expectedThreeMonthRange
      threeMonth.toDateRange(now).count() shouldBe 90
    }
  }

  context("compareTo should return correct comparison") {
    test("Comparison between different periods") {
      zero shouldBeLessThan week
      week shouldBeLessThan twoWeek
      twoWeek shouldBeLessThan month
      month shouldBeLessThan twoMonth
      twoMonth shouldBeLessThan threeMonth
    }

    test("Same period comparison") {
      week shouldBeEqualComparingTo week
      month shouldBeEqualComparingTo month
    }
  }

  context("minusDays should return correct period") {
    test("Reducing period within valid range") {
      week.minusDays(3).days shouldBe 4
      month.minusDays(15).days shouldBe 15
    }

    test("Reducing period to zero") {
      twoWeek.minusDays(14).days shouldBe 0
    }

    test("Reducing period to negative should not be allowed") {
      val reduced = week.minusDays(10)
      reduced.days shouldBe -3
    }
  }

  test("entries should return all defined periods") {
    val expectedEntries = arrayOf(
      Period.Zero,
      Period.Week,
      Period.TwoWeek,
      Period.Month,
      Period.TwoMonth,
      Period.ThreeMonth
    )

    Period.entries() shouldContainExactly expectedEntries
  }

  test("hashCode should be consistent and unique") {
    val week1 = Period.Week
    val week2 = Period.Week
    val customPeriod = Period(7)

    week1.hashCode() shouldBe week2.hashCode()

    week1.hashCode() shouldNotBe twoWeek.hashCode()

    customPeriod.hashCode() shouldBe week1.hashCode()
  }

  test("equals should correctly compare Period instances") {
    val week1 = Period.Week
    val week2 = Period.Week
    val customPeriod = Period(7)
    val differentType = "Not a Period"

    week1 shouldBe week1

    week1 shouldBe week2
    week2 shouldBe week1

    week1 shouldBe customPeriod
    customPeriod shouldBe week2
    week1 shouldBe week2

    week1 shouldBe week2
    week1 shouldBe week2

    week1 shouldNotBe twoWeek

    week1.equals(differentType) shouldBe false

    (week1 == null) shouldBe false
  }
})
