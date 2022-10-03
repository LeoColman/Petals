package br.com.colman.petals.use.pause.repository

import br.com.colman.petals.PauseQueries
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import br.com.colman.petals.Pause as PauseEntity

class PauseRepository(
  private val pauseQueries: PauseQueries
) {

  fun get(): Pause? {
    return pauseQueries.selectFirst().executeAsOneOrNull()?.toPause()
  }

  fun set(pause: Pause) {
    delete()
    pauseQueries.insert(pause.toEntity())
  }

  fun delete() {
    pauseQueries.deleteAll()
  }
}

fun PauseEntity.toPause() = Pause(
  LocalTime.parse(start_time),
  LocalTime.parse(end_time),
  id
)

fun Pause.toEntity() = PauseEntity(
  startTime.format(DateTimeFormatter.ISO_LOCAL_TIME),
  endTime.format(DateTimeFormatter.ISO_LOCAL_TIME),
  id
)
