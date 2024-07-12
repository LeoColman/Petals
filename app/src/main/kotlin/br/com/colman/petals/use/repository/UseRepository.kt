package br.com.colman.petals.use.repository

import br.com.colman.petals.UseQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime.now
import java.time.LocalDateTime.parse
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.*
import br.com.colman.petals.Use as UseEntity

class UseRepository(
  private val useQueries: UseQueries
) {

  fun upsertAll(uses: Iterable<Use>) {
    uses.forEach(::upsert)
  }

  fun upsert(use: Use) {
    Timber.d("Adding use: $use")
    useQueries.upsert(use.toEntity())
  }

  fun getLastUse() = useQueries.selectLast().asFlow().mapToOneOrNull().map { it?.toUse() }

  fun getLastUseDate() = getLastUse().map { it?.date }

  suspend fun repeatLastUse() {
    val lastUse = getLastUse().firstOrNull()
    val newUse = lastUse?.copy(date = now(), id = UUID.randomUUID().toString())
    newUse?.let {
      upsert(it)
    }
  }

  fun all(): Flow<List<Use>> = useQueries.selectAll().asFlow().mapToList().map { it.map(UseEntity::toUse) }

  fun delete(use: Use) {
    Timber.d("Deleting use: $use")
    useQueries.delete(use.id)
  }
}

fun Use.toEntity(): UseEntity = UseEntity(
  date.format(ISO_LOCAL_DATE_TIME),
  amountGrams.toPlainString(),
  costPerGram.toPlainString(),
  id
)

fun UseEntity.toUse() = Use(
  parse(date),
  amount_grams.toBigDecimal(),
  cost_per_gram.toBigDecimal(),
  id
)
