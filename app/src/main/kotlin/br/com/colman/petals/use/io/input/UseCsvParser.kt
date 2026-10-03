package br.com.colman.petals.use.io.input

import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.use.repository.ConsumptionMethod
import br.com.colman.petals.use.repository.Use
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.UUID.randomUUID

/**
 * One CSV line: the use, and the strain the line names, if any. [use] has no strain id yet, because the line's strain
 * may already be in the catalog under another id; [UseImporter] links them once it knows.
 */
data class UseCsvRow(val use: Use, val strain: Strain?)

object UseCsvParser {
  private val csvReader = csvReader()

  fun parse(line: String): Result<UseCsvRow> = runCatching {
    val values = csvReader.readAll(line).single()

    val dateTime = parseDateTime(values[0])
    val amount = values[1].toBigDecimal()
    val cost = values[2].toBigDecimal()
    val id = parseOrGenerateUUID(values.getOrNull(3))
    val description = values.getOrElse(4) { "" }
    val consumptionMethod = ConsumptionMethod.fromKey(values.getOrElse(5) { "" })

    UseCsvRow(Use(dateTime, amount, cost, id, description, consumptionMethod), parseStrain(values))
  }

  /**
   * A line names a strain only when it has a strain name: an id alone can't recreate the strain on another install.
   * Potencies are optional, so an unreadable one is dropped rather than failing the whole line.
   */
  private fun parseStrain(values: List<String>): Strain? {
    val name = values.getOrElse(7) { "" }.trim()
    if (name.isEmpty()) return null

    return Strain(
      name,
      values.getOrNull(8)?.toBigDecimalOrNull(),
      values.getOrNull(9)?.toBigDecimalOrNull(),
      id = parseOrGenerateUUID(values.getOrNull(6))
    )
  }

  private fun parseDateTime(date: String) = LocalDateTime.parse(date, ISO_LOCAL_DATE_TIME)

  private fun parseOrGenerateUUID(uuid: String?) = if (uuid.isNullOrBlank()) randomUUID().toString() else uuid
}
