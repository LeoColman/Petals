package br.com.colman.petals.strain.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import br.com.colman.petals.SelectStrainUses
import br.com.colman.petals.StrainQueries
import br.com.colman.petals.use.repository.costOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.text.Collator
import br.com.colman.petals.Strain as StrainEntity

class StrainRepository(
  private val strainQueries: StrainQueries
) {

  /**
   * Every strain, archived ones included, in the order of the user's language. SQLite's NOCASE only folds ASCII,
   * so Cyrillic or accented names would sort capitals apart from lowercase; a Collator sorts them as people read.
   */
  fun all(dispatcher: CoroutineDispatcher = IO): Flow<List<Strain>> =
    strainQueries.selectAll().asFlow().mapToList(dispatcher).map { it.toSortedStrains() }.flowOn(dispatcher)

  /** The same strains as [all], read once and in no particular order. For an import, which doesn't care. */
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
   * What the uses logged with each strain add up to, by strain id, for every strain that has any. Reads only the
   * columns it adds up, and adds them up on [dispatcher].
   */
  fun summaries(dispatcher: CoroutineDispatcher = IO): Flow<Map<String, StrainSummary>> =
    strainQueries.selectStrainUses().asFlow().mapToList(dispatcher)
      .map { rows -> rows.groupBy { it.strain_id!! }.mapValues { (_, uses) -> uses.toSummary() } }
      .flowOn(dispatcher)

  /**
   * Deletes [strain] only while no use refers to it, and says whether it did. Callers offer archiving for a strain
   * that has been used, so past uses keep their name and potency; this only refuses, it never archives.
   */
  fun delete(strain: Strain): Boolean = strainQueries.transactionWithResult {
    val isUnused = countUses(strain) == 0L
    if (isUnused) strainQueries.delete(strain.id)
    isUnused
  }
}

private fun List<SelectStrainUses>.toSummary() = StrainSummary(
  size,
  sumOf { it.amount_grams.toBigDecimal() },
  sumOf { costOf(it.amount_grams.toBigDecimal(), it.cost_per_gram.toBigDecimal()) },
  mapNotNull { it.rating }.takeIf { it.isNotEmpty() }?.average()
)

private fun List<StrainEntity>.toSortedStrains(): List<Strain> {
  val collator = Collator.getInstance()
  return map(StrainEntity::toStrain).sortedWith(compareBy(collator) { it.name })
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
