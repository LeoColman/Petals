package br.com.colman.petals.strain

import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.totalCost
import br.com.colman.petals.use.repository.totalGrams
import java.math.BigDecimal

/** What the uses logged with one strain add up to. [averageRating] counts only the rated ones, and is null for none. */
data class StrainSummary(
  val uses: Int,
  val grams: BigDecimal,
  val spent: BigDecimal,
  val averageRating: Double?
)

/** A [StrainSummary] for every strain these uses were logged with, by strain id. Uses without a strain are left out. */
fun List<Use>.summariesByStrain(): Map<String, StrainSummary> = filter { it.strainId != null }
  .groupBy { it.strainId!! }
  .mapValues { (_, uses) ->
    StrainSummary(
      uses.size,
      uses.totalGrams,
      uses.totalCost,
      uses.mapNotNull { it.rating }.takeIf { it.isNotEmpty() }?.average()
    )
  }
