package br.com.colman.petals.hittimer.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import br.com.colman.petals.HitQueries
import br.com.colman.petals.hittimer.HitTimer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalDateTime.parse
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import br.com.colman.petals.Hit as HitEntity

class HitRepository(
  private val hitQueries: HitQueries
) {

  /** Inserts, or updates the row already written for this run. Future-dated rows are excluded on read. */
  fun upsert(hit: Hit) {
    hitQueries.upsert(hit.toEntity())
  }

  fun all(dispatcher: CoroutineDispatcher = IO): Flow<List<Hit>> =
    hitQueries.selectAll().asFlow().mapToList(dispatcher).map { it.map(HitEntity::toHit) }

  fun deleteAll() {
    hitQueries.deleteAll()
  }
}

/**
 * Records the timer's current run, if it has one. Keyed on the run's id, so calling this on pause and
 * again on reset updates a single row rather than logging the same hit twice.
 */
fun HitRepository.record(hitTimer: HitTimer, now: LocalDateTime = LocalDateTime.now()) {
  val elapsed = hitTimer.elapsedMillis()
  if (elapsed <= 0) return

  upsert(Hit(now, Duration.ofMillis(elapsed), hitTimer.runId))
}

fun Hit.toEntity(): HitEntity = HitEntity(id, date.format(ISO_LOCAL_DATE_TIME), duration.toMillis())

fun HitEntity.toHit() = Hit(parse(date), Duration.ofMillis(duration_millis), id)
