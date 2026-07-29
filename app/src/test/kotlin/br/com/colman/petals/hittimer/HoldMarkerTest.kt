package br.com.colman.petals.hittimer

import com.jjoe64.graphview.series.DataPoint
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

class HoldMarkerTest : FunSpec({

  val weak = getSubjectiveHighWeakSeries()

  test("At rest the marker sits at the start of the curve") {
    holdPointOn(weak, 0.0, 25.0) shouldBe (0.0 to 30.0)
  }

  test("The marker interpolates between the study's measurements") {
    val (x, y) = holdPointOn(weak, 5.0, 25.0)

    x shouldBe (5.0 plusOrMinus 1e-9)
    y shouldBe (35.0 plusOrMinus 1e-9)
  }

  test("The marker lands on the peak at ten seconds, which is the whole point of the chart") {
    holdPointOn(weak, 10.0, 25.0) shouldBe (10.0 to 40.0)
    holdPointOn(getSubjectiveHighStrongSeries(), 10.0, 25.0) shouldBe (10.0 to 47.0)
  }

  test("Past the peak the marker descends, showing a longer hold buys less") {
    val peak = holdPointOn(weak, 10.0, 25.0).second
    val late = holdPointOn(weak, 15.0, 25.0).second

    late shouldBe (37.5 plusOrMinus 1e-9)
    (late < peak) shouldBe true
  }

  test("A hold longer than the study's data stops at its last measurement") {
    holdPointOn(weak, 40.0, 25.0) shouldBe (20.0 to 35.0)
  }

  test("The marker never leaves the visible chart") {
    val (x, _) = holdPointOn(weak, 40.0, 15.0)

    x shouldBe (15.0 plusOrMinus 1e-9)
  }

  test("A negative hold is pulled back to the start") {
    holdPointOn(weak, -5.0, 25.0) shouldBe (0.0 to 30.0)
  }

  test("Unordered points are handled") {
    val shuffled = listOf(DataPoint(20.0, 35.0), DataPoint(0.0, 30.0), DataPoint(10.0, 40.0))

    holdPointOn(shuffled, 5.0, 25.0) shouldBe holdPointOn(weak, 5.0, 25.0)
  }

  test("Overtime walks the marker down the far side of the peak") {
    val peak = holdPointOn(weak, 10.0, 25.0).second

    listOf(12.0, 15.0, 18.0).map { holdPointOn(weak, it, 25.0).second }.forEach { (it < peak) shouldBe true }
  }
})
