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

import android.content.res.Resources
import br.com.colman.petals.R.string.amount_label
import br.com.colman.petals.R.string.cost_per_gram_label
import br.com.colman.petals.R.string.date_label
import br.com.colman.petals.R.string.id_label
import br.com.colman.petals.use.repository.UseRepository
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.coroutines.flow.first
import java.io.ByteArrayOutputStream
import kotlin.text.Charsets.UTF_8

data class UseCsvHeaders(val date: String, val amount: String, val costPerGram: String, val id: String) {
  constructor(resources: Resources) : this(
    resources.getString(date_label),
    resources.getString(amount_label),
    resources.getString(cost_per_gram_label),
    resources.getString(id_label)
  )

  fun toList() = listOf(date, amount, costPerGram, id)
}

class UseCsvSerializer(
  private val useRepository: UseRepository,
  private val useCsvHeaders: UseCsvHeaders
) {

  suspend fun computeUseCsv(): String {
    val uses = useRepository.all().first().map { it.columns() }
    val content = listOf(useCsvHeaders.toList()) + uses

    return serialize(content)
  }

  private val csvWriter = csvWriter {
    lineTerminator = "\n"
  }

  private fun serialize(content: List<List<String>>): String {
    val strOutput = ByteArrayOutputStream()
    csvWriter.writeAll(content, strOutput)
    return strOutput.toByteArray().toString(UTF_8)
  }
}
