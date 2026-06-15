package br.com.colman.petals.statistics.graph.data

import br.com.colman.petals.use.pause.repository.Pause
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalTime

class BreakPeriodBandTest : FunSpec({
  test("non-midnight pause produces a single range spanning start to end hour") {
    val pause = Pause(LocalTime.of(9, 0), LocalTime.of(12, 0))

    breakPeriodRanges(listOf(pause)) shouldBe listOf(9f to 12f)
  }

  test("minutes are reflected as fractional hours on the axis") {
    val pause = Pause(LocalTime.of(9, 30), LocalTime.of(10, 15))

    breakPeriodRanges(listOf(pause)) shouldBe listOf(9.5f to 10.25f)
  }

  test("cross-midnight pause is split into two ranges") {
    val pause = Pause(LocalTime.of(22, 0), LocalTime.of(6, 0))

    breakPeriodRanges(listOf(pause)) shouldBe listOf(22f to 23f, 0f to 6f)
  }

  test("disabled pauses are excluded from ranges and edges") {
    val pause = Pause(LocalTime.of(9, 0), LocalTime.of(12, 0), isEnabled = false)

    breakPeriodRanges(listOf(pause)) shouldBe emptyList()
    breakPeriodEdges(listOf(pause)) shouldBe emptyList()
  }

  test("edges mark each enabled pause start and end") {
    val pauses = listOf(
      Pause(LocalTime.of(9, 0), LocalTime.of(12, 0)),
      Pause(LocalTime.of(20, 0), LocalTime.of(21, 0), isEnabled = false)
    )

    breakPeriodEdges(pauses) shouldBe listOf(9f, 12f)
  }
})
