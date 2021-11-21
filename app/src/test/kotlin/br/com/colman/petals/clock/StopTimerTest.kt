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

package br.com.colman.petals.clock

import androidx.test.core.app.ApplicationProvider
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.joda.time.LocalDateTime.now
import kotlin.math.abs

@RobolectricTest
class StopTimerTest : ShouldSpec({

    val target by lazy { QuitTimer(ApplicationProvider.getApplicationContext()) }

    should("Persist and return the start date") {
        val now = now()

        target.setQuitDate(now)

        target.quitDate.first() shouldBe now
    }

    should("Update the clock every ~1ms") {
        val now = now()

        target.setQuitDate(now)

        val periods = target.periodSinceQuit.take(100).toList().windowed(2)
        val timeDifferenceAverage = periods.map { (first, second) ->
            abs(first.millis - second.millis)
        }.average()


        timeDifferenceAverage shouldBe (1.0 plusOrMinus 1.0)
    }
})
