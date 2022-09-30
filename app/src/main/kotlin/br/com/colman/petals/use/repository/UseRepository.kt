package br.com.colman.petals.use.repository

import br.com.colman.petals.UseQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime.parse
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import br.com.colman.petals.Use as UseEntity

class UseRepository(
  private val useQueries: UseQueries
) {

  fun insertAll(uses: Iterable<Use>) {
    uses.forEach(::insert)
  }

  fun insert(use: Use) {
    Timber.d("Adding use: $use")
    useQueries.insert(use.toEntity())
  }

  fun getLastUse() = all().map { it.maxByOrNull { it.date } }

  fun getLastUseDate() = getLastUse().map { it?.date }

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
