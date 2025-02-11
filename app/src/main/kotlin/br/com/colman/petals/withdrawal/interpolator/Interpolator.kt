package br.com.colman.petals.withdrawal.interpolator

import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction
import java.time.Duration

class Interpolator(
  val data: Map<Duration, Double>
) : UnivariateInterpolator, UnivariateFunction {

  private val dataInterpolator: PolynomialSplineFunction = LinearInterpolator().interpolate(
    data.keys.map { it.seconds.toDouble() }.toDoubleArray(),
    data.values.toDoubleArray()
  )

  private val maxPossibleX = data.keys.max().seconds

  override fun interpolate(xs: DoubleArray?, ys: DoubleArray?): UnivariateFunction = dataInterpolator

  override fun value(x: Double): Double = dataInterpolator.value(x.coerceAtMost(maxPossibleX.toDouble()))
}
