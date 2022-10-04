package br.com.colman.petals.utils

import java.time.LocalTime
import java.time.temporal.ChronoUnit

fun LocalTime.truncatedToMinute(): LocalTime = truncatedTo(ChronoUnit.MINUTES)
