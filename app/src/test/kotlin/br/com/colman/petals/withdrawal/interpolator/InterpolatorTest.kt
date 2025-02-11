package br.com.colman.petals.withdrawal.interpolator

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.math3.analysis.UnivariateFunction
import java.time.Duration.ofSeconds as seconds

class InterpolatorTest : FunSpec({

  test("Should respect maxPossibleX") {
    val data = mapOf(
      seconds(0) to 0.0,
      seconds(5) to 10.0,
      seconds(10) to 20.0
    )
    val target = Interpolator(data)

    target.value(10.0) shouldBe 20.0
    target.value(100.0) shouldBe 20.0
  }

  test("should implement UnivariateFunction") {
    val data = mapOf(
      seconds(0) to 0.0,
      seconds(2) to 4.0
    )
    val target = Interpolator(data)

    target.value(1.0) shouldBe 2.0
  }

  test("should interpolate ignoring given arrays") {
    val data = mapOf(
      seconds(0) to 0.0,
      seconds(10) to 10.0
    )

    val interpolator = Interpolator(data)
    val univariateFunction: UnivariateFunction = interpolator.interpolate(
      doubleArrayOf(),
      doubleArrayOf()
    )

    univariateFunction.value(5.0) shouldBe 5.0
  }
})
