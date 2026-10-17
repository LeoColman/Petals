package br.com.colman.petals.statistics.graph.data

import br.com.colman.petals.statistics.graph.data.TimeOfDayResolution.Hour
import br.com.colman.petals.statistics.graph.data.TimeOfDayResolution.QuarterHour
import br.com.colman.petals.use.repository.Use
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalTime

private fun useAt(hour: Int, minute: Int, grams: String, day: Int = 16) =
  Use(LocalDate.of(2026, 10, day).atTime(hour, minute), grams.toBigDecimal())

class DistributionPerHourTest : FunSpec({
  // A break ending at 20:30, two uses in its last minutes and one after it.
  val uses = listOf(useAt(20, 20, "0.3"), useAt(20, 25, "0.4"), useAt(20, 50, "0.2"))

  test("by the hour, every use from 20:00 to 20:59 adds up in the 20:00 bucket") {
    val grams = gramsPerBucket(uses, Hour)

    grams shouldHaveSize 24
    grams[20] shouldBe 0.9f
    grams.sum() shouldBe 0.9f
  }

  test("by the quarter hour, uses before and after 20:30 land in separate buckets") {
    val grams = gramsPerBucket(uses, QuarterHour)

    grams shouldHaveSize 96
    grams[QuarterHour.bucketOf(LocalTime.of(20, 15))] shouldBe 0.7f
    grams[QuarterHour.bucketOf(LocalTime.of(20, 30))] shouldBe 0f
    grams[QuarterHour.bucketOf(LocalTime.of(20, 45))] shouldBe 0.2f
  }

  test("uses on different days add up in the same bucket") {
    val acrossDays = listOf(useAt(8, 5, "0.1"), useAt(8, 10, "0.2", day = 1))

    gramsPerBucket(acrossDays, QuarterHour)[QuarterHour.bucketOf(LocalTime.of(8, 0))] shouldBe 0.3f
  }

  test("no uses gives an empty day rather than no points") {
    gramsPerBucket(emptyList(), QuarterHour) shouldBe List(96) { 0f }
  }
})
