/*
 * Petals APP
 * Copyright (C) 2021 Leonardo Colman Lopes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package br.com.colman.petals.withdrawal.discomfort.repository

import br.com.colman.petals.clock.LastUseDateRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
import org.joda.time.Days.days
import org.joda.time.LocalDateTime.now
import org.joda.time.Seconds.secondsBetween

private fun daysInSeconds(days: Int) = days(days).toStandardSeconds().seconds

class DiscomfortRepository(
  private val lastUseDateRepository: LastUseDateRepository
) {

  val discomfort = flow {
    while(true) {
      delay(100)
      emit(calculateDiscomfort())
    }
  }

  /**
   * (Budney, J Abnorm Psychol, 2003)
   *
   * DOI 10.1037/0021-843x.112.3.393
   *
   * The article gives the data in days, and we'll transform them here.
   * Baseline/0.0 indicates the starting point when an individual starts the abstinence period
   */
  val discomfortDays = mapOf(
    days(0) to 8.0,
    days(1) to 8.0,
    days(2) to 8.0,
    days(3) to 8.0,
    days(4) to 7.5,
    days(5) to 7.5,
    days(6) to 7.5,
    days(7) to 6.5,
    days(8) to 6.5,
    days(9) to 6.5,
    days(10) to 5.2,
    days(11) to 5.2,
    days(12) to 5.2,
    days(13) to 5.0,
    days(14) to 5.0,
    days(15) to 5.0,
    days(16) to 5.0,
    days(17) to 5.0,
    days(18) to 5.0,
    days(19) to 4.0,
    days(20) to 4.0,
    days(21) to 4.0,
    days(22) to 3.5,
    days(23) to 3.5,
    days(24) to 3.5,
    days(25) to 3.0,
  )

  private val discomfortSeconds =
    discomfortDays.mapKeys { (days, _) -> days.toStandardSeconds().seconds.toDouble() }

  private val interpolator = SplineInterpolator().interpolate(
    discomfortSeconds.keys.toDoubleArray(),
    discomfortSeconds.values.toDoubleArray()
  )

  private suspend fun calculateDiscomfort(): Discomfort {
    val quitDate = lastUseDateRepository.quitDate.filterNotNull().first()
    val secondsQuit = secondsBetween(quitDate, now()).seconds
    return Discomfort(interpolator.value(secondsQuit.toDouble()))
  }

  data class Discomfort(val discomfortStrength: Double)

}