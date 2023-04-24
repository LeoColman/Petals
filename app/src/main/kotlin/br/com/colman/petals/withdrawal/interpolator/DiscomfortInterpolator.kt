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
 *
 * Values approximated from Figure 2
 */
val DiscomfortDataPoints = mapOf(
  days(0) to Discomfort(8.0),
  days(1) to Discomfort(8.0),
  days(2) to Discomfort(8.0),
  days(3) to Discomfort(8.0),
  days(4) to Discomfort(7.5),
  days(5) to Discomfort(7.5),
  days(6) to Discomfort(7.5),
  days(7) to Discomfort(6.5),
  days(8) to Discomfort(6.5),
  days(9) to Discomfort(6.5),
  days(10) to Discomfort(5.2),
  days(11) to Discomfort(5.2),
  days(12) to Discomfort(5.2),
  days(13) to Discomfort(5.0),
  days(14) to Discomfort(5.0),
  days(15) to Discomfort(5.0),
  days(16) to Discomfort(5.0),
  days(17) to Discomfort(5.0),
  days(18) to Discomfort(5.0),
  days(19) to Discomfort(4.0),
  days(20) to Discomfort(4.0),
  days(21) to Discomfort(4.0),
  days(22) to Discomfort(3.5),
  days(23) to Discomfort(3.5),
  days(24) to Discomfort(3.5),
  days(25) to Discomfort(3.0),
)

@JvmInline
value class Discomfort(val strength: Double)

class DiscomfortInterpolator : UnivariateInterpolator, UnivariateFunction {

  val discomfortSeconds =
    DiscomfortDataPoints.mapKeys { it.key.seconds.toDouble() }.mapValues { it.value.strength }

  private val splineFunction: PolynomialSplineFunction = SplineInterpolator().interpolate(
    discomfortSeconds.keys.toDoubleArray(),
    discomfortSeconds.values.toDoubleArray()
  )

  override fun interpolate(xval: DoubleArray?, yval: DoubleArray?) = splineFunction

  override fun value(x: Double): Double = splineFunction.value(x)

  fun calculateDiscomfort(secondsQuit: Long) =
    value(secondsQuit.toDouble().coerceAtMost(discomfortSeconds.keys.max()))
}
