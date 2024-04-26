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

import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.interpolator.DiscomfortDataPoints
import br.com.colman.petals.withdrawal.interpolator.Interpolator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit

class DiscomfortRepository(
  private val useRepository: UseRepository
) {
  private val interpolator = Interpolator(DiscomfortDataPoints)

  val discomfort = flow {
    while (true) {
      delay(100)
      emit(calculateDiscomfort())
    }
  }

  private suspend fun calculateDiscomfort(): Double {
    val quitDate = useRepository.getLastUseDate().firstOrNull() ?: return 0.0
    val secondsQuit = ChronoUnit.SECONDS.between(quitDate, now()).coerceAtLeast(0)

    return interpolator.value(secondsQuit.toDouble())
  }
}
