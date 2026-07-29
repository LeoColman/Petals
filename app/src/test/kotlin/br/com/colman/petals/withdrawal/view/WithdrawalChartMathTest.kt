package br.com.colman.petals.withdrawal.view

import br.com.colman.petals.withdrawal.interpolator.Interpolator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import java.time.Duration

class WithdrawalChartMathTest : FunSpec({

  val curve = mapOf(Duration.ofDays(0) to 3.5, Duration.ofDays(2) to 7.5, Duration.ofDays(25) to 3.0)

  test("Scaling puts the curve minimum at zero and its maximum at the ceiling") {
    val scaled = curve.scaled(10.0)

    scaled[Duration.ofDays(25)] shouldBe (0.0 plusOrMinus 1e-9)
    scaled[Duration.ofDays(2)] shouldBe (10.0 plusOrMinus 1e-9)
    scaled[Duration.ofDays(0)] shouldBe (1.111 plusOrMinus 0.001)
  }

  test("A curve with no spread collapses to zero instead of NaN") {
    val flat = mapOf(Duration.ofDays(0) to 4.0, Duration.ofDays(5) to 4.0)

    flat.scaled(10.0).values.forEach { it shouldBe 0.0 }
  }

  test("The current point sits at the abstinence time, in days") {
    val interpolator = Interpolator(curve.scaled(10.0))

    val (x, y) = chartPointFor(interpolator, Duration.ofDays(2), 25.0)

    x shouldBe (2.0 plusOrMinus 1e-9)
    y shouldBe (10.0 plusOrMinus 1e-9)
  }

  test("Half days are not truncated away") {
    val interpolator = Interpolator(curve.scaled(10.0))

    chartPointFor(interpolator, Duration.ofHours(12), 25.0).first shouldBe (0.5 plusOrMinus 1e-9)
  }

  test("A reading past the chart edge is pulled back into view") {
    val interpolator = Interpolator(curve.scaled(10.0))

    chartPointFor(interpolator, Duration.ofDays(40), 20.0).first shouldBe 20.0
    chartPointFor(interpolator, Duration.ofDays(40), 25.0).first shouldBe 25.0
  }

  test("A negative reading is pulled back to zero") {
    val interpolator = Interpolator(curve.scaled(10.0))

    chartPointFor(interpolator, Duration.ofDays(-5), 25.0).first shouldBe 0.0
  }
})
