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

package br.com.colman.petals.withdrawal.repository

import br.com.colman.petals.clock.QuitTimer
import br.com.colman.petals.withdrawal.thc.repository.ThcConcentrationRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.percent
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.joda.time.LocalDateTime
import org.joda.time.LocalDateTime.now
import kotlin.reflect.full.functions

class ThcConcentrationRepositoryTest : FunSpec({
    val now = now()
    mockkStatic(*(LocalDateTime::class.functions.filter { it.name == "now" }.toTypedArray()))
    every { now() } returns now

    val quitTimer = mockk<QuitTimer>()
    val target = ThcConcentrationRepository(quitTimer)

    test("On start concentration is 100%") {
        every { quitTimer.quitDate } returns flowOf(now)

        target.concentration.first().percentageOnBodyFromStart shouldBe 100.0.plusOrMinus(5.percent)
    }

    test("At the end concentration should be 0%") {
        every { quitTimer.quitDate } returns flowOf(now.plusDays(14))

        target.concentration.first().percentageOnBodyFromStart shouldBe 0.0.plusOrMinus(0.1)
    }

    test("After the end should still show 0%") {
        every { quitTimer.quitDate } returns flowOf(now.plusDays(230))

        target.concentration.first().percentageOnBodyFromStart shouldBe 0.0
    }

    afterSpec { clearAllMocks() }

})
