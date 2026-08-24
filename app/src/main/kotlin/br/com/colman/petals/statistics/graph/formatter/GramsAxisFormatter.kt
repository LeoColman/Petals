package br.com.colman.petals.statistics.graph.formatter

import java.util.Locale

/**
 * Y axis labels for a grams axis, with as many decimals as the axis actually needs.
 *
 * A day of use is often a fraction of a gram and a month of it is several, and one fixed precision
 * cannot serve both: whole numbers collapse a 0.32g axis into a column of zeroes, while two decimals
 * on a 30g axis is noise.
 */
fun gramsAxisFormatter(maxValue: Float): (Float) -> String {
  val decimals = when {
    maxValue < 1f -> 2
    maxValue < 10f -> 1
    else -> 0
  }

  return { value -> "%.${decimals}f".format(Locale.US, value) }
}
