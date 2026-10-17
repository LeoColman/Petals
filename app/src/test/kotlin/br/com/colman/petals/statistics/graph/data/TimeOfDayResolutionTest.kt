package br.com.colman.petals.statistics.graph.data

import br.com.colman.petals.statistics.graph.data.TimeOfDayResolution.Hour
import br.com.colman.petals.statistics.graph.data.TimeOfDayResolution.QuarterHour
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalTime

class TimeOfDayResolutionTest : FunSpec({
  test("an hour splits the day in 24 buckets ending at 23") {
    Hour.bucketCount shouldBe 24
    Hour.lastHour shouldBe 23f
  }

  test("a quarter hour splits the day in 96 buckets ending at 23.75") {
    QuarterHour.bucketCount shouldBe 96
    QuarterHour.lastHour shouldBe 23.75f
  }

  test("20:14 and 20:15 fall in neighbouring quarter-hour buckets") {
    QuarterHour.hourOf(QuarterHour.bucketOf(LocalTime.of(20, 14))) shouldBe 20f
    QuarterHour.hourOf(QuarterHour.bucketOf(LocalTime.of(20, 15))) shouldBe 20.25f
  }

  test("20:59 falls in the 20:00 hour bucket") {
    Hour.hourOf(Hour.bucketOf(LocalTime.of(20, 59))) shouldBe 20f
  }

  test("midnight falls in the first bucket and 23:59 in the last") {
    QuarterHour.bucketOf(LocalTime.MIDNIGHT) shouldBe 0
    QuarterHour.bucketOf(LocalTime.of(23, 59)) shouldBe 95
    Hour.bucketOf(LocalTime.of(23, 59)) shouldBe 23
  }

  test("the quarter-hour line has no dots, the hourly one keeps them") {
    QuarterHour.drawsDots shouldBe false
    Hour.drawsDots shouldBe true
  }

  test("a switch that is on stands for the quarter hour") {
    TimeOfDayResolution.of(isQuarterHour = true) shouldBe QuarterHour
    TimeOfDayResolution.of(isQuarterHour = false) shouldBe Hour
  }
})
