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

package br.com.colman.petals.use.repository

import kotlinx.coroutines.flow.first

class UseExporter(
  private val useRepository: UseRepository
) {

  suspend fun toCsvFileContent(
    dateLabel: String,
    amountLabel: String,
    costPerGramLabel: String
  ): String {
    val headers = "$dateLabel,$amountLabel,$costPerGramLabel"
    val uses = useRepository.all().first().map { toCsvLine(it) }
    return (listOf(headers) + uses).joinToString("\n")
  }

  fun toCsvLine(use: Use) = """
    "${use.date}","${use.amountGrams}","${use.costPerGram}"
  """.trimIndent()

}
