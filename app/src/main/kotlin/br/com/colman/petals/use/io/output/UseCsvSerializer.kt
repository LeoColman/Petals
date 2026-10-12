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
import br.com.colman.petals.R
import br.com.colman.petals.R.string.amount_label
import br.com.colman.petals.R.string.consumption_method_label
import br.com.colman.petals.R.string.cost_per_gram_label
import br.com.colman.petals.R.string.date_label
import br.com.colman.petals.R.string.id_label
import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.use.repository.Rating
import br.com.colman.petals.use.repository.UseRepository
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.coroutines.flow.first
import java.io.ByteArrayOutputStream
import kotlin.text.Charsets.UTF_8

/**
 * The CSV header. The use's own columns are labelled in the app's language; the strain's and the rating keep the
 * fixed labels of [Strain.CsvHeader] and [Rating.CsvColumn], which the importer finds them by.
 */
data class UseCsvHeaders(
  val date: String,
  val amount: String,
  val costPerGram: String,
  val id: String,
  val description: String,
  val consumptionMethod: String
) {
  constructor(resources: Resources) : this(
    resources.getString(date_label),
    resources.getString(amount_label),
    resources.getString(cost_per_gram_label),
    resources.getString(id_label),
    resources.getString(R.string.description_label),
    resources.getString(consumption_method_label)
  )

  fun toList() = listOf(date, amount, costPerGram, id, description, consumptionMethod) + Strain.CsvHeader +
    Rating.CsvColumn
}

class UseCsvSerializer(
  private val useRepository: UseRepository,
  private val useCsvHeaders: UseCsvHeaders
) {

  /**
   * Every use, each followed by its strain's columns and its rating. A use without a strain, or whose strain is gone,
   * gets empty strain columns, which reads back as no strain, and an unrated use an empty rating.
   */
  suspend fun computeUseCsv(): String {
    val lines = useRepository.allWithStrains().first().map { (use, strain) ->
      use.columns() + (strain?.columns() ?: NoStrainColumns) + use.rating?.let(Rating::format).orEmpty()
    }
    val content = listOf(useCsvHeaders.toList()) + lines

    return serialize(content)
  }

  private val csvWriter = csvWriter {
    lineTerminator = "\n"
    outputLastLineTerminator = false
  }

  private fun serialize(content: List<List<String>>): String {
    val strOutput = ByteArrayOutputStream()
    csvWriter.writeAll(content, strOutput)
    return strOutput.toByteArray().toString(UTF_8)
  }
}

private val NoStrainColumns = List(Strain.CsvColumnCount) { "" }
