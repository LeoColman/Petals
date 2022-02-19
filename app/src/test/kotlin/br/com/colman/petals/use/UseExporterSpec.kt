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

package br.com.colman.petals.use

import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseExporter
import br.com.colman.petals.use.repository.UseRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.asFlow
import java.math.BigDecimal.ZERO


class UseExporterSpec : FunSpec({

  val useRepository = mockk<UseRepository>()
  val target = UseExporter(useRepository)

  test("Converts use to csv") {
    val use = useArb.next()

    target.toCsvLine(use) shouldBe """
      "${use.date}","${use.amountGrams}","${use.costPerGram}"
    """.trimIndent()
  }

  context("Create CSV content") {
    val uses = useArb.take(1000).windowed(10).toList()
    every { useRepository.all() } returns uses.asFlow()

    val file = target.toCsvFileContent("date", "amount", "cost")

    test("Includes all values in resulting file") {
      file shouldContain uses.flatMap { it.map { target.toCsvLine(it) } }.joinToString("\n")
    }

    test("Includes the headers") {
      file shouldStartWith "date,amount,cost\n"
    }

  }

})

val useArb = arbitrary {
  val bigDecimals = Arb.bigDecimal(ZERO, 100.0.toBigDecimal())
  Use(
    bigDecimals.next(it),
    bigDecimals.next(it),
    Arb.localDateTime().next(it)
  )
}

