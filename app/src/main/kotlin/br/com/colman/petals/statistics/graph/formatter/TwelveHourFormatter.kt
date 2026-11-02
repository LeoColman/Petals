package br.com.colman.petals.statistics.graph.formatter

import info.appdev.charting.components.AxisBase
import info.appdev.charting.formatter.IAxisValueFormatter
import kotlin.math.roundToInt

@Suppress("MagicNumber")
val TwelveHourFormatter = object : IAxisValueFormatter {
  override fun getFormattedValue(value: Float, axis: AxisBase?): String {
    if (value.roundToInt() == 12) return "12"
    return (value.roundToInt() % 12).toString()
  }
}
