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

package br.com.colman.petals.use.io.output

import android.content.res.Resources
import br.com.colman.petals.R.string.amount_label
import br.com.colman.petals.R.string.cost_per_gram_label
import br.com.colman.petals.R.string.date_label
import br.com.colman.petals.R.string.description_label
import br.com.colman.petals.R.string.id_label
import br.com.colman.petals.use.UseArb
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.arbitrary.take
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal
import java.time.LocalDateTime

class UseCsvSerializerTest : FunSpec({
  val useRepository = mockk<UseRepository>()
  val useCsvHeaders = UseCsvHeaders("date", "amount", "cost", "id", "description")
  val target = UseCsvSerializer(useRepository, useCsvHeaders)

  test("Includes all values in resulting file") {
    val uses = UseArb.take(10).toList()
    val usesCsv = uses.map { it.columns().joinToString(",") }
    every { useRepository.all() } returns flowOf(uses)

    val file = target.computeUseCsv()

    file shouldEndWith usesCsv.joinToString("\n")
  }

  test("Includes the headers at the start of the file") {
    val uses = UseArb.take(10).toList()
    every { useRepository.all() } returns flowOf(uses)

    val file = target.computeUseCsv()

    file shouldStartWith "date,amount,cost,id,description\n"
  }

  test("Produces CSV with only headers when there is no data") {
    val emptyUses = emptyList<Use>()
    every { useRepository.all() } returns flowOf(emptyUses)

    val file = target.computeUseCsv()

    file shouldBe "date,amount,cost,id,description"
  }

  test("Initializes headers from resources correctly") {
    val resources = mockk<Resources> {
      every { getString(date_label) } returns "a"
      every { getString(amount_label) } returns "b"
      every { getString(cost_per_gram_label) } returns "c"
      every { getString(id_label) } returns "d"
      every { getString(description_label) } returns "e"
    }

    val localizedHeaders = UseCsvHeaders(resources)

    localizedHeaders.toList() shouldBe listOf("a", "b", "c", "d", "e")
  }

  test("Throws exception when data retrieval fails") {
    every { useRepository.all() } throws RuntimeException("Data retrieval failed")

    shouldThrow<RuntimeException> {
      target.computeUseCsv()
    }.message shouldBe "Data retrieval failed"
  }

  test("Data in CSV output matches data from repository") {
    val uses = UseArb.take(10).toList()
    every { useRepository.all() } returns flowOf(uses)

    val file = target.computeUseCsv()

    val csvLines = file.lines().drop(1)
    val expectedCsvLines = uses.map { it.columns().joinToString(",") }

    csvLines shouldBe expectedCsvLines
  }

  test("Formats date and numbers correctly") {
    val use = Use(
      id = "d483262c-ed3f-4457-8e23-2302c8c7a43e",
      date = LocalDateTime.of(2024, 2, 9, 12, 34, 56),
      amountGrams = BigDecimal("1234.56"),
      costPerGram = BigDecimal("78.90")
    )
    val uses = listOf(use)
    every { useRepository.all() } returns flowOf(uses)

    val file = target.computeUseCsv()

    val expectedDataLine = "2024-02-09T12:34:56,1234.56,78.90,d483262c-ed3f-4457-8e23-2302c8c7a43e,"

    file shouldContain expectedDataLine
  }

  test("Handles headers with non-ASCII characters") {
    val localizedHeaders = UseCsvHeaders("ã", "æ", "̉ħ", "ŋ", "®")
    val targetWithLocalizedHeaders = UseCsvSerializer(useRepository, localizedHeaders)
    val uses = UseArb.take(1).toList()
    every { useRepository.all() } returns flowOf(uses)

    val file = targetWithLocalizedHeaders.computeUseCsv()

    file shouldStartWith "ã,æ,̉ħ,ŋ,®\n"
  }
})
