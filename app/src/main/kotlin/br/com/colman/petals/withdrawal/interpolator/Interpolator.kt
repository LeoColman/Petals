package br.com.colman.petals.withdrawal.interpolator

import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction
import java.time.Duration
import java.time.Duration.ofDays as days

class Interpolator(
  val data: Map<Duration, Double>
) : UnivariateInterpolator, UnivariateFunction {

  private fun splineFunction(): PolynomialSplineFunction = SplineInterpolator().interpolate(
    data.keys.map { it.seconds.toDouble() }.toDoubleArray(),
    data.values.toDoubleArray()
  )

  override fun interpolate(xs: DoubleArray?, ys: DoubleArray?): UnivariateFunction = splineFunction()

  override fun value(x: Double): Double = splineFunction().value(x)

  fun calculatePercentage(secondsQuit: Long): Double {
    val maxValue = data.values.max()
    val currentValue = value(secondsQuit.coerceAtMost(data.keys.max().seconds).toDouble())
    return currentValue / maxValue
  }
}

/**
 * (Budney, J Abnorm Psychol, 2003)
 *
 * DOI 10.1037/0021-843x.112.3.393
 *
 * The article gives the data in days, and we'll transform them here.
 * Baseline/0.0 indicates the starting point when an individual starts the abstinence period
 *
 * Values approximated from Figure 2
 */
val DiscomfortDataPoints = mapOf(
  days(-2) to 3.0,
  days(-1) to 3.0,
  days(0) to 3.0,
  days(1) to 8.0,
  days(2) to 8.0,
  days(3) to 8.0,
  days(4) to 7.5,
  days(5) to 7.5,
  days(6) to 7.5,
  days(7) to 6.5,
  days(8) to 6.5,
  days(9) to 6.5,
  days(10) to 5.2,
  days(11) to 5.2,
  days(12) to 5.2,
  days(13) to 5.0,
  days(14) to 5.0,
  days(15) to 5.0,
  days(16) to 5.0,
  days(17) to 5.0,
  days(18) to 5.0,
  days(19) to 4.0,
  days(20) to 4.0,
  days(21) to 4.0,
  days(22) to 3.5,
  days(23) to 3.5,
  days(24) to 3.5,
  days(25) to 3.0,
  days(45) to 3.0,
)

/**
 * (Budney, J Abnorm Psychol, 2003)
 *
 * DOI 10.1037/0021-843x.112.3.393
 *
 * The article gives the data in days, and we'll transform them here.
 * Baseline/0.0 indicates the starting point when an individual starts the abstinence period
 */
val ThcConcentrationDataPoints = mapOf(
  days(0) to 250.0,
  days(1) to 175.0,
  days(2) to 125.0,
  days(3) to 100.0,
  days(4) to 75.0,
  days(7) to 50.0,
  days(10) to 45.0,
  days(14) to 25.0,
  days(20) to 0.0 // Approximate based on Table 2
)
