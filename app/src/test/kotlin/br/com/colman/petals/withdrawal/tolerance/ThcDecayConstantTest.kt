package br.com.colman.petals.withdrawal.tolerance

import br.com.colman.petals.withdrawal.data.ThcConcentrationDataPoints
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.math.ln

/**
 * Pins [ThcEliminationPerDay] to the data the app already ships, so nobody can quietly replace it with a
 * rounder number. Fitting a shorter range gives a materially different constant (truncating at day 7 gives
 * a 3.0 day half-life instead of 4.6), and the constant sets how strongly a reduction registers.
 */
class ThcDecayConstantTest : FunSpec({

  val positivePoints = ThcConcentrationDataPoints.filterValues { it > 0.0 }

  test("Lambda is the log-linear fit of the shipped THC curve") {
    val xs = positivePoints.keys.map { it.toDays().toDouble() }
    val ys = positivePoints.values.map { ln(it) }
    val meanX = xs.average()
    val meanY = ys.average()

    val slope = xs.zip(ys).sumOf { (x, y) -> (x - meanX) * (y - meanY) } / xs.sumOf { (it - meanX) * (it - meanX) }

    -slope shouldBe (ThcEliminationPerDay plusOrMinus 1e-9)
  }

  test("Half life is four and a half days") {
    ln(2.0) / ThcEliminationPerDay shouldBe (4.594 plusOrMinus 0.001)
  }

  test("The fit excludes exactly one point, the day twenty zero, since ln(0) is undefined") {
    positivePoints.size shouldBe ThcConcentrationDataPoints.size - 1
    ThcConcentrationDataPoints.values.count { it <= 0.0 } shouldBe 1
  }
})
