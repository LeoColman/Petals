package br.com.colman.petals.use.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import br.com.colman.petals.UseQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime.parse
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
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

  fun getLastUse() = useQueries.selectLast().asFlow().mapToOneOrNull(Dispatchers.IO).map { it?.toUse() }

  fun getLastUseDate() = getLastUse().map { it?.date }

  fun all(): Flow<List<Use>> = useQueries.selectAll().asFlow().mapToList(
    Dispatchers.IO
  ).map { it.map(UseEntity::toUse) }

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
  description
)

fun UseEntity.toUse() = Use(
  parse(date),
  amount_grams.toBigDecimal(),
  cost_per_gram.toBigDecimal(),
  id,
  description
)
