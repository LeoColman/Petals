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

package br.com.colman.petals.use.io.input

import app.cash.sqldelight.Transacter
import br.com.colman.petals.strain.repository.StrainRepository
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository

class UseImporter(
  private val useRepository: UseRepository,
  private val strainRepository: StrainRepository,
  private val transacter: Transacter
) {

  /**
   * Saves every use in the CSV file [csv], and returns how many. The first row is skipped when it isn't a use, as it's
   * the header then; any other row that isn't a use fails the import, which then saves nothing.
   */
  fun import(csv: String, modifyUse: (Use) -> (Use) = { it }): Result<Int> = runCatching {
    val csvRows = UseCsvParser.rowsOf(csv)
    val strainColumns = csvRows.firstOrNull()?.let(UseCsvParser::strainColumnsIn)
    val rows = csvRows.mapIndexed { index, values ->
      UseCsvParser.parse(values, strainColumns).onFailure {
        if (index > 0) throw it
      }
    }.mapNotNull { it.getOrNull() }

    transacter.transaction {
      val strains = StrainResolver(strainRepository.allNow())
      val resolved = strains.resolveAll(rows.map { it.strain })
      val uses = rows.zip(resolved) { row, strain -> modifyUse(row.use.copy(strainId = strain?.id)) }

      strainRepository.upsertAll(strains.created)
      // An import links strains but never unlinks them: a line that names no strain, from before strains or not,
      // leaves an existing use's strain alone, so no backup can wipe the links made since it was taken.
      useRepository.upsertAllKeepingStrains(uses)
    }
    rows.size
  }
}
