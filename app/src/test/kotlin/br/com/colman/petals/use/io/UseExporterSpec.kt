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

package br.com.colman.petals.use.io

import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import com.natpryce.snodge.mutants
import com.natpryce.snodge.text.replaceWithPossiblyMeaningfulText
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.bigDecimal
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.take
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal.ZERO
import kotlin.random.Random

class UseExporterSpec : FunSpec({
  val useRepository = mockk<UseRepository>()
  val useCsvHeaders = UseCsvHeaders("date", "amount", "cost")
  val target = UseCsvSerializer(useRepository, useCsvHeaders)

  context("Create CSV content") {
    val uses = useArb.take(1000).toList()
    val usesCsv = uses.map { it.columns().joinToString(",") }
    every { useRepository.all() } returns flowOf(uses)

    val file = target.computeUseCsv()

    test("Includes all values in resulting file") {
      file shouldContain usesCsv.joinToString("\n")
    }

    test("Included the headers at the start of the file") {
      file shouldStartWith "date,amount,cost\n"
    }
  }
})

val useArb = arbitrary {
  val bigDecimals = Arb.bigDecimal(ZERO, 100.0.toBigDecimal())
  Use(
    Arb.localDateTime().next(it),
    bigDecimals.next(it),
    bigDecimals.next(it)
  )
}

val useCsvArb = useArb.map { it.columns().joinToString(",") }

val invalidUseCsvArb = useCsvArb.map {
  Random.mutants(replaceWithPossiblyMeaningfulText(), 1, it)
}.map { it.single() }
