package br.com.colman.petals.statistics.graph.formatter

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.util.Locale

val GramsValueFormatter = object : IValueFormatter {
  override fun getFormattedValue(
    value: Float,
    entry: Entry?,
    dataSetIndex: Int,
    viewPortHandler: ViewPortHandler?
  ): String {
    return "%.2f".format(Locale.US, entry?.y ?: 0.0) + "g"
  }
}
