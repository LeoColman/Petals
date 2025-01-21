package br.com.colman.petals.use.pause.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import br.com.colman.petals.PauseQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import br.com.colman.petals.Pause as PauseEntity

class PauseRepository(
  private val pauseQueries: PauseQueries
) {

  fun getAll(dispatcher: CoroutineDispatcher = IO) = pauseQueries.selectAll().asFlow()
    .mapToList(dispatcher).map { pauses ->
      pauses.map(PauseEntity::toPause).sortedWith(compareBy({ it.startTime }, { it.endTime }))
    }

  fun get(dispatcher: CoroutineDispatcher = IO): Flow<Pause?> {
    return pauseQueries.selectFirst().asFlow().mapToOneOrNull(dispatcher).map { it?.toPause() }
  }

  fun insert(pause: Pause) {
    pauseQueries.insert(pause.toEntity())
  }

  fun update(pause: Pause) {
    val pauseEntity = pause.toEntity()
    pauseQueries.update(pauseEntity.start_time, pauseEntity.end_time, pauseEntity.is_enabled, pauseEntity.id)
  }

  fun delete(pause: Pause) {
    pauseQueries.delete(pause.id)
  }
}

fun PauseEntity.toPause() = Pause(
  LocalTime.parse(start_time),
  LocalTime.parse(end_time),
  id,
  is_enabled == 1L
)

fun Pause.toEntity() = PauseEntity(
  startTime.format(DateTimeFormatter.ISO_LOCAL_TIME),
  endTime.format(DateTimeFormatter.ISO_LOCAL_TIME),
  id,
  if (isEnabled) 1 else 0
)
