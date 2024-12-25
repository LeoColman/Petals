package br.com.colman.petals.utils

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit.MINUTES

fun LocalDateTime.truncatedToMinute(): LocalDateTime = truncatedTo(MINUTES)
fun LocalTime.truncatedToMinute(): LocalTime = truncatedTo(MINUTES)
