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

  fun import(csvFileLines: List<String>, modifyUse: (Use) -> (Use) = { it }): Result<Unit> = runCatching {
    val rows = csvFileLines.mapIndexed { index, s ->
      UseCsvParser.parse(s).onFailure {
        if (index > 0) throw it
      }
    }.mapNotNull { it.getOrNull() }

    val strains = StrainResolver(strainRepository.allNow())
    val uses = rows.map { (use, strain) -> modifyUse(use.copy(strainId = strain?.let(strains::resolve)?.id)) }

    transacter.transaction {
      strainRepository.upsertAll(strains.created)
      useRepository.upsertAll(uses)
    }
  }
}
