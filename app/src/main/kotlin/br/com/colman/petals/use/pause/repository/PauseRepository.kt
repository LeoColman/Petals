package br.com.colman.petals.use.pause.repository

import br.com.colman.petals.PauseQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import br.com.colman.petals.Pause as PauseEntity

class PauseRepository(
  private val pauseQueries: PauseQueries
) {

  fun getAll(): Flow<List<Pause>> {
    return pauseQueries.selectAll().asFlow().mapToList().map { it.map { pauseEntity -> pauseEntity.toPause() } }
  }

  fun get(): Flow<Pause?> {
    return pauseQueries.selectFirst().asFlow().mapToOneOrNull().map { it?.toPause() }
  }

  fun insert(pause: Pause) {
    pauseQueries.insert(pause.toEntity())
  }

  fun update(pause: Pause) {
    val pauseEntity = pause.toEntity()
    pauseQueries.updatePauseById(pauseEntity.start_time, pauseEntity.end_time, pauseEntity.is_disabled, pauseEntity.id)
  }

  fun delete(pause: Pause) {
    pauseQueries.deleteById(pause.id)
  }

  fun delete() {
    pauseQueries.deleteAll()
  }
}

fun PauseEntity.toPause() = Pause(
  LocalTime.parse(start_time),
  LocalTime.parse(end_time),
  id,
  (is_disabled ?: 0) != 0L // Zero is default value because pause is not disabled by default
)

fun Pause.toEntity() = PauseEntity(
  startTime.format(DateTimeFormatter.ISO_LOCAL_TIME),
  endTime.format(DateTimeFormatter.ISO_LOCAL_TIME),
  id,
  if (isDisabled) 1 else 0
)
