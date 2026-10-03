package br.com.colman.petals.use.io.input

import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.use.repository.ConsumptionMethod
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.utils.toBoundedDecimalOrNull
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.UUID.randomUUID

/**
 * One CSV line: the use, and the strain the line names, if any. [use] has no strain id yet, because the line's strain
 * may already be in the catalog under another id; [UseImporter] links them once [StrainResolver] knows.
 */
data class UseCsvRow(val use: Use, val strain: CsvStrain?)

/**
 * A strain as a CSV line describes it. Unlike a [Strain] it may have no [id], when the line gave none, so it can't
 * be saved as it is: [StrainResolver] matches it to the catalog or gives it an id first.
 */
data class CsvStrain(
  val id: String?,
  val name: String,
  val thcPercent: BigDecimal? = null,
  val cbdPercent: BigDecimal? = null,
  val costPerGram: BigDecimal? = null,
  val isArchived: Boolean = false
) {
  val nameKey: String get() = Strain.nameKey(name)

  fun toStrain(id: String) = Strain(name, thcPercent, cbdPercent, costPerGram, isArchived, id)
}

object UseCsvParser {
  private val csvReader = csvReader()
  private val Percentages = BigDecimal.ZERO..BigDecimal(100)

  // Where the strain's columns start: right after the use's own, in the order Strain.columns() writes them.
  private const val StrainColumns = 6
  private const val StrainId = StrainColumns
  private const val StrainName = StrainColumns + 1
  private const val StrainThc = StrainColumns + 2
  private const val StrainCbd = StrainColumns + 3
  private const val StrainCost = StrainColumns + 4
  private const val StrainArchived = StrainColumns + 5

  fun parse(line: String): Result<UseCsvRow> = runCatching {
    val values = csvReader.readAll(line).single()

    val dateTime = parseDateTime(values[0])
    val amount = parseDecimal(values[1])
    val cost = parseDecimal(values[2])
    val id = parseOrGenerateUUID(values.getOrNull(3))
    val description = values.getOrElse(4) { "" }
    val consumptionMethod = ConsumptionMethod.fromKey(values.getOrElse(5) { "" })

    UseCsvRow(Use(dateTime, amount, cost, id, description, consumptionMethod), parseStrain(values))
  }

  /**
   * A line names a strain only when it has a strain name: an id alone can't recreate the strain on another install.
   * Every strain value is optional, so one that can't be read is dropped rather than failing the whole line.
   */
  private fun parseStrain(values: List<String>): CsvStrain? {
    val name = values.getOrElse(StrainName) { "" }.trim()
    if (name.isEmpty()) return null

    return CsvStrain(
      values.getOrNull(StrainId)?.trim()?.ifEmpty { null },
      name,
      parsePercentage(values.getOrNull(StrainThc)),
      parsePercentage(values.getOrNull(StrainCbd)),
      values.getOrNull(StrainCost)?.trim()?.toBoundedDecimalOrNull()?.takeIf { it >= BigDecimal.ZERO },
      values.getOrNull(StrainArchived)?.trim().let { it.equals("true", ignoreCase = true) || it == "1" }
    )
  }

  private fun parseDecimal(value: String): BigDecimal =
    requireNotNull(value.toBoundedDecimalOrNull()) { "Not a number this app can store: $value" }

  /** A trailing percent sign, as a spreadsheet formats the column, is fine. */
  private fun parsePercentage(value: String?) =
    value?.trim()?.removeSuffix("%")?.trim()?.toBoundedDecimalOrNull()?.takeIf { it in Percentages }

  private fun parseDateTime(date: String) = LocalDateTime.parse(date, ISO_LOCAL_DATE_TIME)

  private fun parseOrGenerateUUID(uuid: String?) = if (uuid.isNullOrBlank()) randomUUID().toString() else uuid
}
