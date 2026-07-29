package br.com.colman.petals.hittimer.repository

import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

/**
 * One recorded breathhold. [id] is stable for the duration of a run, so pausing, resuming and pausing
 * again updates the same row instead of logging the same hit several times.
 */
data class Hit(
  val date: LocalDateTime,
  val duration: Duration,
  val id: String = UUID.randomUUID().toString()
)
