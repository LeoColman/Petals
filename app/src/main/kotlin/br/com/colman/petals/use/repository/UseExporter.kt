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

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.coroutines.flow.first
import java.io.ByteArrayOutputStream
import kotlin.text.Charsets.UTF_8

class UseExporter(
  private val useRepository: UseRepository
) {

  private val csvWriter = csvWriter {
    lineTerminator = "\n"
  }

  suspend fun toCsvFileContent(
    dateLabel: String,
    amountLabel: String,
    costPerGramLabel: String
  ): String {
    val headers = listOf(dateLabel, amountLabel, costPerGramLabel)
    val uses = useRepository.all().first().map { it.columns() }

    val headerUses = listOf(headers) + uses

    val strOutput = ByteArrayOutputStream()

    csvWriter.writeAll(headerUses, strOutput)

    return strOutput.toByteArray().toString(UTF_8)
  }
}
