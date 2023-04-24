package br.com.colman.petals.withdrawal.interpolator

import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction
import java.time.Duration.ofDays as days

/**
 * (Budney, J Abnorm Psychol, 2003)
 *
 * DOI 10.1037/0021-843x.112.3.393
 *
 * The article gives the data in days, and we'll transform them here.
 * Baseline/0.0 indicates the starting point when an individual starts the abstinence period
 */
val ThcConcentrationDataPoints = mapOf(
  days(0) to ThcConcentration(250.0),
  days(1) to ThcConcentration(175.0),
  days(2) to ThcConcentration(125.0),
  days(3) to ThcConcentration(100.0),
  days(4) to ThcConcentration(75.0),
  days(7) to ThcConcentration(50.0),
  days(10) to ThcConcentration(45.0),
  days(14) to ThcConcentration(25.0),
  days(20) to ThcConcentration(0.0) // Approximate based on Table 2
)

class ThcConcentrationInterpolator : UnivariateInterpolator, UnivariateFunction {

  private val thcSecondQuitToConcentration =
    ThcConcentrationDataPoints.map { (key, value) -> key.toSeconds().toDouble() to value.nanoGramPerMilliliter }
      .toMap()

  private val splineFunction: PolynomialSplineFunction = SplineInterpolator().interpolate(
    thcSecondQuitToConcentration.keys.toDoubleArray(),
    thcSecondQuitToConcentration.values.toDoubleArray()
  )

  override fun interpolate(xval: DoubleArray, yval: DoubleArray) = splineFunction

  override fun value(x: Double): Double = splineFunction.value(x)

  fun calculatePercentage(secondsQuit: Long): Double {
    val maxConcentration = thcSecondQuitToConcentration.values.max()
    val current = value(secondsQuit.toDouble().coerceAtMost(thcSecondQuitToConcentration.keys.max()))

    return current / maxConcentration
  }
}

@JvmInline
value class ThcConcentration(val nanoGramPerMilliliter: Double)
