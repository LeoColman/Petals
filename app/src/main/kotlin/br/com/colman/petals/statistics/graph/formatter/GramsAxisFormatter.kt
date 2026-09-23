package br.com.colman.petals.statistics.graph.formatter

import java.util.Locale
import kotlin.math.ceil
import kotlin.math.log10

private const val MaxDecimals = 4

/**
 * Y axis labels for a grams axis, with as many decimals as the axis actually needs.
 *
 * A day of use is often a fraction of a gram and a month of it is several, and one fixed precision
 * cannot serve both: whole numbers collapse a 0.32g axis into a column of zeroes, while two decimals
 * on a 30g axis is noise. Below a tenth of a gram the precision keeps following the magnitude, so an
 * axis topping out at 0.004g still tells its ticks apart rather than printing 0.00 five times.
 */
fun gramsAxisFormatter(maxValue: Float): (Float) -> String {
  val decimals = when {
    maxValue >= 10f -> 0
    maxValue >= 1f -> 1
    maxValue > 0f -> (ceil(-log10(maxValue.toDouble())).toInt() + 1).coerceIn(2, MaxDecimals)
    else -> 2
  }

  return { value -> "%.${decimals}f".format(Locale.US, value) }
}
