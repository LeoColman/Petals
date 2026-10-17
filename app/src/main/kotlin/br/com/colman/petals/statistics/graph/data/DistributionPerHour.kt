package br.com.colman.petals.statistics.graph.data

import androidx.compose.ui.graphics.toArgb
import br.com.colman.petals.statistics.graph.color.createColor
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalGrams
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineDataSet.Mode.HORIZONTAL_BEZIER

/**
 * The grams used in each [resolution] bucket of the day, counting from midnight.
 * Plain arithmetic with no chart types, so it is unit-testable.
 */
fun gramsPerBucket(uses: List<Use>, resolution: TimeOfDayResolution): List<Float> {
  val usesPerBucket = uses.groupBy { resolution.bucketOf(it.date.toLocalTime()) }
  return List(resolution.bucketCount) { bucket -> usesPerBucket[bucket].orEmpty().totalGrams.toFloat() }
}

/** One period's grams by time of day, one entry per [resolution] bucket, placed where the bucket starts. */
fun createDistributionPerHourDataset(
  days: Int,
  uses: List<Use>,
  label: String,
  resolution: TimeOfDayResolution
): LineDataSet {
  val entries = gramsPerBucket(uses, resolution).mapIndexed { bucket, grams ->
    Entry(resolution.hourOf(bucket), grams)
  }
  return LineDataSet(entries, label).apply {
    setDrawCircles(resolution.drawsDots)
    setDrawFilled(true)
    setDrawValues(false)
    fillColor = createColor(days).toArgb()
    color = createColor(days).toArgb()
    setCircleColor(createColor(days).toArgb())
    mode = HORIZONTAL_BEZIER
  }
}
