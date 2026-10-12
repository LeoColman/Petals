package br.com.colman.petals.use.repository

import kotlin.math.roundToInt

/** How a use is rated: stars from half a star to five, in halves. */
object Rating {
  const val Max = 5.0

  /**
   * The label of the rating's CSV column. Like the strain's, it is never translated: the importer finds the column by
   * it, so a file from before ratings, or a column a user added, is never read as a rating.
   */
  const val CsvColumn = "rating"

  /** [stars] rounded to the nearest half star, or null when that is no rating, below half a star or above five. */
  fun ofOrNull(stars: Double): Double? = ((stars * 2).roundToInt() / 2.0).takeIf { it in 0.5..Max }

  /** "4" for four stars and "3.5" for three and a half, as the CSV writes them. */
  fun format(stars: Double): String = if (stars % 1.0 == 0.0) stars.toInt().toString() else stars.toString()
}
