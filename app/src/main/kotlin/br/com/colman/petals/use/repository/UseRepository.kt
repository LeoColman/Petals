package br.com.colman.petals.use.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import br.com.colman.petals.SelectAllWithStrain
import br.com.colman.petals.UseQueries
import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.strain.repository.toStrain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.time.LocalDateTime.parse
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import br.com.colman.petals.Strain as StrainEntity
import br.com.colman.petals.Use as UseEntity

class UseRepository(
  private val useQueries: UseQueries
) {

  fun upsertAll(uses: Iterable<Use>) {
    useQueries.transaction {
      uses.forEach(::upsert)
    }
  }

  fun upsert(use: Use) {
    useQueries.upsert(use.toEntity())
  }

  /**
   * Like [upsertAll], except that a use without a strain doesn't take away the strain an existing use has. For
   * imports, which link strains but never unlink them.
   */
  fun upsertAllKeepingStrains(uses: Iterable<Use>) {
    useQueries.transaction {
      uses.forEach { useQueries.upsertKeepingStrain(it.toEntity()) }
    }
  }

  fun getLastUse(dispatcher: CoroutineDispatcher = IO) =
    useQueries.selectLast().asFlow().mapToOneOrNull(dispatcher).map { it?.toUse() }

  fun getLastUseDate() = getLastUse().map { it?.date }

  fun countAll(dispatcher: CoroutineDispatcher = IO) =
    useQueries.countAll().asFlow().mapToOneOrNull(dispatcher).filterNotNull().map { it.toInt() }

  fun all(dispatchers: CoroutineDispatcher = IO): Flow<List<Use>> = useQueries.selectAll().asFlow().mapToList(
    dispatchers
  ).map { it.map(UseEntity::toUse) }

  /**
   * Uses from [from] up to now, oldest first. Future-dated rows are excluded, matching [getLastUse].
   * Callers that only need a recent window should prefer this over [all], which materialises every row.
   */
  fun since(from: LocalDateTime, dispatcher: CoroutineDispatcher = IO): Flow<List<Use>> =
    useQueries.selectSince(from.format(ISO_LOCAL_DATE_TIME)).asFlow().mapToList(dispatcher)
      .map { it.map(UseEntity::toUse) }

  /**
   * Every use with the strain it was logged with, read in one query so a use and its strain always come from the
   * same moment. The strain is null for a use without one, or whose strain is gone.
   */
  fun allWithStrains(dispatcher: CoroutineDispatcher = IO): Flow<List<Pair<Use, Strain?>>> =
    useQueries.selectAllWithStrain().asFlow().mapToList(dispatcher)
      .map { it.map(SelectAllWithStrain::toUseAndStrain) }

  fun delete(use: Use) {
    Timber.d("Deleting use: $use")
    useQueries.delete(use.id)
  }
}

fun Use.toEntity(): UseEntity = UseEntity(
  date.format(ISO_LOCAL_DATE_TIME),
  amountGrams.toPlainString(),
  costPerGram.toPlainString(),
  id,
  description,
  consumptionMethod?.key.orEmpty(),
  strainId
)

fun UseEntity.toUse() = Use(
  parse(date),
  amount_grams.toBigDecimal(),
  cost_per_gram.toBigDecimal(),
  id,
  description,
  ConsumptionMethod.fromKey(consumption_method),
  strain_id
)

private fun SelectAllWithStrain.toUseAndStrain(): Pair<Use, Strain?> {
  val use = UseEntity(date, amount_grams, cost_per_gram, id, description, consumption_method, strain_id).toUse()
  val strain = if (strain_id == null || strain_name == null) {
    null
  } else {
    StrainEntity(
      strain_id,
      strain_name,
      strain_thc_percent,
      strain_cbd_percent,
      strain_cost_per_gram,
      strain_is_archived ?: 0
    ).toStrain()
  }
  return use to strain
}
