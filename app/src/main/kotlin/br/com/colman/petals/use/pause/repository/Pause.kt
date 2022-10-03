package br.com.colman.petals.use.pause.repository

import java.time.LocalTime
import java.util.UUID

data class Pause(
  val startTime: LocalTime = LocalTime.now(),
  val endTime: LocalTime = LocalTime.now(),
  var id: String = UUID.randomUUID().toString()
) {

  @Transient
  private val passesThroughMidnight = startTime > endTime

  fun isActive(time: LocalTime = LocalTime.now()) = if (passesThroughMidnight) {
    time.isAfter(startTime) || time.isBefore(endTime)
  } else time in startTime..endTime
}
