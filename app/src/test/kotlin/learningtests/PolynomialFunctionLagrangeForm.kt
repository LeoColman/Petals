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

package learningtests

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.property.Arb
import io.kotest.property.arbitrary.numericDoubles
import io.kotest.property.checkAll
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator

class PolynomialInterpolatorTest : FunSpec({

    test("Test polynomial interpolation") {
        val points = mapOf(
            0.0 to 250.0,
            1.0 to 175.0,
            2.0 to 125.0,
            3.0 to 100.0,
            4.0 to 75.0,
            7.0 to 50.0,
            10.0 to 50.0,
            14.0 to 25.0
        )

        val function = SplineInterpolator().interpolate(points.keys.toDoubleArray(), points.values.toDoubleArray())

        // Value between 0 and 1, expected output should be between 250 and 175 as the function is decreasing always
        function.value(0.5).shouldBeBetween(175.0, 250.0, 0.5)

        // Same thing goes for some other values
        function.value(3.5).shouldBeBetween(75.0, 100.0, 0.5)
        function.value(12.0).shouldBeBetween(25.0, 50.0, 0.5)

        Arb.numericDoubles(0.1, 14.0).checkAll {
            function.value(it).shouldBeBetween(260.0, 0.0, 0.1)
        }

    }
})
