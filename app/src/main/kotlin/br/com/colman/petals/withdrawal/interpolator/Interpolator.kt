package br.com.colman.petals.withdrawal.interpolator

import java.time.Duration
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction

class Interpolator(
  val data: Map<Duration, Double>
) : UnivariateInterpolator, UnivariateFunction {

  private val dataInterpolator: PolynomialSplineFunction = LinearInterpolator().interpolate(
    data.keys.map { it.seconds.toDouble() }.toDoubleArray(),
    data.values.toDoubleArray()
  )

  override fun interpolate(xs: DoubleArray?, ys: DoubleArray?): UnivariateFunction = dataInterpolator

  override fun value(x: Double): Double = dataInterpolator.value(x)

  fun calculatePercentage(secondsQuit: Long): Double {
    val maxValue = data.values.max()
    val currentValue = value(secondsQuit.coerceAtMost(data.keys.max().seconds).toDouble())
    return currentValue / maxValue
  }
}
