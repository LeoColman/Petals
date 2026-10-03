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
  private val Uuid = Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")

  /**
   * Where the strain's columns start in a file with this [header], or null when it has none. They are found by their
   * fixed labels, in [Strain.CsvHeader] order, so a file from before strains, a file without a header, or columns a
   * user added in a spreadsheet are never read as a strain.
   */
  fun strainColumnsIn(header: String): Int? = runCatching { csvReader.readAll(header).single() }.getOrNull()
    ?.map { it.trim() }
    ?.windowed(Strain.CsvColumnCount)
    ?.indexOfFirst { it == Strain.CsvHeader }
    ?.takeIf { it >= 0 }

  /** Reads a line, and its strain from the columns starting at [strainColumns], if the file has them. */
  fun parse(line: String, strainColumns: Int? = null): Result<UseCsvRow> = runCatching {
    val values = csvReader.readAll(line).single()

    val dateTime = parseDateTime(values[0])
    val amount = parseDecimal(values[1])
    val cost = parseDecimal(values[2])
    val id = parseOrGenerateUUID(values.getOrNull(3))
    val description = values.getOrElse(4) { "" }
    val consumptionMethod = ConsumptionMethod.fromKey(values.getOrElse(5) { "" })
    val strain = strainColumns?.let { parseStrain { label -> values.getOrNull(it + Strain.CsvHeader.indexOf(label)) } }

    UseCsvRow(Use(dateTime, amount, cost, id, description, consumptionMethod), strain)
  }

  /**
   * A line names a strain only when it has a strain name: an id alone can't recreate the strain on another install.
   * Every strain value is optional, so one that can't be read is dropped rather than failing the whole line. Only a
   * UUID counts as an id, as the app writes them; a hand-made id like "1" could collide with another file's, so such
   * a strain is matched by name instead.
   */
  private fun parseStrain(valueOf: (String) -> String?): CsvStrain? {
    val name = valueOf(Strain.NameColumn)?.trim().orEmpty()
    if (name.isEmpty()) return null

    return CsvStrain(
      valueOf(Strain.IdColumn)?.trim()?.takeIf { it.matches(Uuid) },
      name,
      parsePercentage(valueOf(Strain.ThcColumn)),
      parsePercentage(valueOf(Strain.CbdColumn)),
      valueOf(Strain.CostColumn)?.trim()?.toBoundedDecimalOrNull()?.takeIf { it >= BigDecimal.ZERO },
      valueOf(Strain.ArchivedColumn)?.trim().let { it.equals("true", ignoreCase = true) || it == "1" }
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
