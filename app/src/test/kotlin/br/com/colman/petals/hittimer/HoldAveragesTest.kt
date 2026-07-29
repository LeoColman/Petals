package br.com.colman.petals.hittimer

import br.com.colman.petals.hittimer.HoldWindow.AllTime
import br.com.colman.petals.hittimer.HoldWindow.Last30Days
import br.com.colman.petals.hittimer.HoldWindow.Last7Days
import br.com.colman.petals.hittimer.repository.Hit
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.time.Duration
import java.time.LocalDateTime

private val Now: LocalDateTime = LocalDateTime.of(2026, 1, 31, 12, 0)

private fun hit(daysAgo: Long, seconds: Double) =
  Hit(Now.minusDays(daysAgo), Duration.ofMillis((seconds * 1000).toLong()))

private fun List<HoldAverage>.of(window: HoldWindow) = single { it.window == window }

class HoldAveragesTest : FunSpec({

  test("No hits leaves every window empty rather than averaging zero") {
    holdAverages(emptyList(), Now).forEach {
      it.average.shouldBeNull()
      it.count shouldBe 0
    }
  }

  test("Every window is reported, longest first") {
    holdAverages(emptyList(), Now).map { it.window } shouldBe listOf(AllTime, Last30Days, Last7Days)
  }

  test("Averages only the hits inside each window") {
    val hits = listOf(
      hit(daysAgo = 2, seconds = 6.0),
      hit(daysAgo = 5, seconds = 10.0),
      hit(daysAgo = 20, seconds = 20.0),
      hit(daysAgo = 200, seconds = 40.0)
    )

    val averages = holdAverages(hits, Now)

    averages.of(Last7Days).average shouldBe Duration.ofSeconds(8)
    averages.of(Last7Days).count shouldBe 2
    averages.of(Last30Days).average shouldBe Duration.ofSeconds(12)
    averages.of(Last30Days).count shouldBe 3
    averages.of(AllTime).average shouldBe Duration.ofSeconds(19)
    averages.of(AllTime).count shouldBe 4
  }

  test("A hit sitting exactly on the window edge is included") {
    val averages = holdAverages(listOf(hit(daysAgo = 7, seconds = 12.0)), Now)

    averages.of(Last7Days).count shouldBe 1
    averages.of(Last7Days).average shouldBe Duration.ofSeconds(12)
  }

  test("A hit one day past the edge is not") {
    val averages = holdAverages(listOf(hit(daysAgo = 8, seconds = 12.0)), Now)

    averages.of(Last7Days).count shouldBe 0
    averages.of(Last7Days).average.shouldBeNull()
    averages.of(Last30Days).count shouldBe 1
  }

  test("Future dated hits are ignored, in every window") {
    val hits = listOf(hit(daysAgo = 1, seconds = 10.0), Hit(Now.plusDays(3), Duration.ofSeconds(60)))

    val averages = holdAverages(hits, Now)

    averages.of(AllTime).count shouldBe 1
    averages.of(AllTime).average shouldBe Duration.ofSeconds(10)
  }

  test("A single hit averages to itself") {
    holdAverages(listOf(hit(daysAgo = 0, seconds = 13.5)), Now).of(AllTime).average shouldBe
      Duration.ofMillis(13_500)
  }
})
