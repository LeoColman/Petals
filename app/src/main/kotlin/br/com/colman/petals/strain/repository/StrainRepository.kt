package br.com.colman.petals.strain.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import br.com.colman.petals.StrainQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import br.com.colman.petals.Strain as StrainEntity

class StrainRepository(
  private val strainQueries: StrainQueries
) {

  /** Every strain, archived ones included, ordered by name ignoring case. */
  fun all(dispatcher: CoroutineDispatcher = IO): Flow<List<Strain>> =
    strainQueries.selectAll().asFlow().mapToList(dispatcher).map { it.map(StrainEntity::toStrain) }

  /** The same as [all], read once. For callers that are not collecting, like an import. */
  fun allNow(): List<Strain> = strainQueries.selectAll().executeAsList().map(StrainEntity::toStrain)

  fun upsert(strain: Strain) {
    strainQueries.upsert(strain.toEntity())
  }

  fun upsertAll(strains: Iterable<Strain>) {
    strainQueries.transaction {
      strains.forEach(::upsert)
    }
  }

  fun countUses(strain: Strain): Long = strainQueries.countUses(strain.id).executeAsOne()

  /**
   * Deletes [strain] only while no use refers to it, and says whether it did. A strain that has been used is
   * archived instead, so past uses keep their name and potency.
   */
  fun delete(strain: Strain): Boolean = strainQueries.transactionWithResult {
    val isUnused = countUses(strain) == 0L
    if (isUnused) strainQueries.delete(strain.id)
    isUnused
  }
}

fun Strain.toEntity(): StrainEntity = StrainEntity(
  id,
  name,
  thcPercent?.toPlainString(),
  cbdPercent?.toPlainString(),
  costPerGram?.toPlainString(),
  if (isArchived) 1 else 0
)

fun StrainEntity.toStrain() = Strain(
  name,
  thc_percent?.toBigDecimal(),
  cbd_percent?.toBigDecimal(),
  cost_per_gram?.toBigDecimal(),
  is_archived == 1L,
  id
)
