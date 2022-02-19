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

package br.com.colman.petals.withdrawal.thc.repository

import br.com.colman.petals.use.repository.UseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
import org.joda.time.Days.days
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit
import kotlin.math.abs

private fun daysInSeconds(days: Int) = days(days).toStandardSeconds().seconds

class ThcConcentrationRepository(
  private val useRepository: UseRepository
) {

  val concentration = flow {
    while(true) {
      delay(100)
      emit(concentration())
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
  val abstinenceThc = mapOf(
    days(0) to 250.0,
    days(1) to 175.0,
    days(2) to 125.0,
    days(3) to 100.0,
    days(4) to 75.0,
    days(7) to 50.0,
    days(10) to 50.0,
    days(14) to 25.0
  )

  private val abstinenceThcSeconds =
    abstinenceThc.mapKeys { (days, _) -> days.toStandardSeconds().seconds.toDouble() }

  private val interpolator = SplineInterpolator().interpolate(
    abstinenceThcSeconds.keys.toDoubleArray(),
    abstinenceThcSeconds.values.toDoubleArray()
  )

  private fun toConcentration(secondsQuit: Int): ThcConcentration {
    val quitCalculate = abs(secondsQuit).coerceIn(0..daysInSeconds(14)).toDouble()

    val target = interpolator.value(daysInSeconds(14).toDouble())
    val baseline = interpolator.value(0.0) - target
    val current = interpolator.value(quitCalculate) - target

    val percentageValue = (current / baseline).coerceIn(0.0, 1.0)
    return ThcConcentration(percentageValue * 100)
  }

  private suspend fun concentration(): ThcConcentration {
    val quitDate = useRepository.getLastUseDate().filterNotNull().first()
    val secondsQuit = ChronoUnit.SECONDS.between(quitDate, now())
    return toConcentration(secondsQuit.toInt())
  }

  data class ThcConcentration(val percentageOnBodyFromStart: Double)

}