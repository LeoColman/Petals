package br.com.colman.petals.statistics.graph.formatter

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.util.Locale

/** An amount printed next to its own point, as "0.42g". */
val GramsLabel: (Float) -> String = { "%.2f".format(Locale.US, it) + "g" }

/** The same label for the charts still drawn by MPAndroidChart. */
val GramsValueFormatter = object : IValueFormatter {
  override fun getFormattedValue(
    value: Float,
    entry: Entry?,
    dataSetIndex: Int,
    viewPortHandler: ViewPortHandler?
  ): String = GramsLabel(entry?.y ?: 0f)
}
