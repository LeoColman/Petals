package br.com.colman.petals.strain.repository

import java.math.BigDecimal

/** What the uses logged with one strain add up to. [averageRating] counts only the rated ones, and is null for none. */
data class StrainSummary(
  val uses: Int,
  val grams: BigDecimal,
  val spent: BigDecimal,
  val averageRating: Double?
) {
  companion object {
    /** A strain no use was logged with. */
    val Unused = StrainSummary(0, BigDecimal.ZERO, BigDecimal.ZERO, null)
  }
}
