package br.com.colman.petals.statistics.graph.formatter

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlin.math.roundToInt

@Suppress("MagicNumber")
val TwelveHourFormatter = object : IAxisValueFormatter {
  override fun getFormattedValue(value: Float, axis: AxisBase?): String {
    if (value.roundToInt() == 12) return "12"
    return (value.roundToInt() % 12).toString()
  }
}
