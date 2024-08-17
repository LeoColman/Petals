package br.com.colman.petals.utils

object ClockFormatHandler {

  private const val HOURS_24 = "24 hours"
  private const val HOURS_12 = "12 hours"

  val formatsList = listOf(HOURS_12, HOURS_24)

  fun is24HourFormat(format: String) = format == HOURS_24

}
