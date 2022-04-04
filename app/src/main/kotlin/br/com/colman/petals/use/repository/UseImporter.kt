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

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

class UseImporter(
  private val useRepository: UseRepository
) {

  fun import(csvFileLines: List<String>): Result<Unit> = runCatching {
    val uses = csvFileLines.mapIndexed { index, s ->
      UseCsvParser.parse(s).onFailure {
        if (index > 0) throw it
      }
    }.mapNotNull { it.getOrNull() }

    useRepository.insertAll(uses)
  }
}

object UseCsvParser {
  private val csvReader = csvReader()

  fun parse(line: String): Result<Use> = runCatching {
    val (date, amount, cost) = csvReader.readAll(line).single()

    val dateTime = LocalDateTime.parse(date, ISO_LOCAL_DATE_TIME)

    Use(dateTime, amount.toBigDecimal(), cost.toBigDecimal())
  }
}
