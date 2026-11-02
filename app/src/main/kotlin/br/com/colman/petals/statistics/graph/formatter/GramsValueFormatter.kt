package br.com.colman.petals.statistics.graph.formatter

import info.appdev.charting.data.EntryFloat
import info.appdev.charting.formatter.IValueFormatter
import info.appdev.charting.utils.ViewPortHandler
import java.util.Locale

val GramsValueFormatter = object : IValueFormatter {
  override fun getFormattedValue(
    value: Float,
    entryFloat: EntryFloat?,
    dataSetIndex: Int,
    viewPortHandler: ViewPortHandler?
  ): String {
    return "%.2f".format(Locale.US, entryFloat?.y ?: 0.0) + "g"
  }
}
