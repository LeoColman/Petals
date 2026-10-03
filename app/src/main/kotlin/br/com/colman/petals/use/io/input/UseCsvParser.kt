package br.com.colman.petals.use.io.input

import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.use.repository.ConsumptionMethod
import br.com.colman.petals.use.repository.Use
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.UUID.randomUUID

/**
 * One CSV line: the use, and the strain the line names, if any. [use] has no strain id yet, because the line's strain
 * may already be in the catalog under another id; [UseImporter] links them once it knows. The strain's id is empty
 * when the line didn't give one, so [StrainResolver] matches it by name only. [hasStrainColumns] tells a line from
 * before strains existed, which says nothing about the strain, from one that says the use had none.
 */
data class UseCsvRow(val use: Use, val strain: Strain?, val hasStrainColumns: Boolean)

object UseCsvParser {
  private val csvReader = csvReader()
  private val Percentages = BigDecimal.ZERO..BigDecimal(100)

  // Plain decimals only. Exponents like 1E-999999999 pass a range check yet make toPlainString build a string
  // of a billion digits, so they are refused before they become numbers.
  private val PlainPercentage = Regex("[0-9]{1,3}([.][0-9]{1,6})?")

  fun parse(line: String): Result<UseCsvRow> = runCatching {
    val values = csvReader.readAll(line).single()

    val dateTime = parseDateTime(values[0])
    val amount = values[1].toBigDecimal()
    val cost = values[2].toBigDecimal()
    val id = parseOrGenerateUUID(values.getOrNull(3))
    val description = values.getOrElse(4) { "" }
    val consumptionMethod = ConsumptionMethod.fromKey(values.getOrElse(5) { "" })

    UseCsvRow(Use(dateTime, amount, cost, id, description, consumptionMethod), parseStrain(values), values.size > 7)
  }

  /**
   * A line names a strain only when it has a strain name: an id alone can't recreate the strain on another install.
   */
  private fun parseStrain(values: List<String>): Strain? {
    val name = values.getOrElse(7) { "" }.trim()
    if (name.isEmpty()) return null

    val id = values.getOrNull(6)?.trim().orEmpty()
    return Strain(name, parsePercentage(values.getOrNull(8)), parsePercentage(values.getOrNull(9)), id = id)
  }

  /** Potencies are optional, so one that isn't a percentage is dropped rather than failing the whole line. */
  private fun parsePercentage(value: String?) =
    value?.trim()?.takeIf { it.matches(PlainPercentage) }?.toBigDecimal()?.takeIf { it in Percentages }

  private fun parseDateTime(date: String) = LocalDateTime.parse(date, ISO_LOCAL_DATE_TIME)

  private fun parseOrGenerateUUID(uuid: String?) = if (uuid.isNullOrBlank()) randomUUID().toString() else uuid
}
