package br.com.colman.petals.statistics.graph.data

import br.com.colman.petals.use.repository.Use
import com.github.mikephil.charting.data.Entry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class DistributionPerDaySinceFirstUseTest : FunSpec({
  val firstDay = LocalDate.of(2026, 9, 1)

  fun use(day: LocalDate, grams: String) = Use(day.atTime(12, 0), grams.toBigDecimal())

  fun List<Entry>.points() = map { it.x to it.y }

  context("grams distribution") {
    test("an empty history has no entries") {
      calculateAllTimeGramsDistribution(emptyList(), today = firstDay).shouldBeEmpty()
    }

    test("one entry per day from the first use to today, with days without use at zero") {
      val uses = listOf(
        use(firstDay.plusDays(2), "1.0"),
        use(firstDay, "0.5"),
        use(firstDay, "0.25"),
      )

      calculateAllTimeGramsDistribution(uses, today = firstDay.plusDays(3)).points() shouldBe listOf(
        0f to 0.75f,
        1f to 0f,
        2f to 1f,
        3f to 0f,
      )
    }

    test("uses at different times of the same day add up on that day") {
      val uses = listOf(
        Use(firstDay.atTime(8, 0), "0.1".toBigDecimal()),
        Use(firstDay.atTime(23, 59), "0.2".toBigDecimal()),
      )

      calculateAllTimeGramsDistribution(uses, today = firstDay).points() shouldBe listOf(0f to 0.3f)
    }
  }

  context("moving average") {
    test("an empty series has no average") {
      calculateMovingAverage(emptyList()).shouldBeEmpty()
    }

    test("each point averages itself and up to six points before it") {
      val entries = (1..10).map { Entry(it + 9f, it.toFloat()) }

      calculateMovingAverage(entries).points() shouldBe listOf(
        10f to 1f,
        11f to 1.5f,
        12f to 2f,
        13f to 2.5f,
        14f to 3f,
        15f to 3.5f,
        16f to 4f,
        17f to 5f,
        18f to 6f,
        19f to 7f,
      )
    }
  }
})
