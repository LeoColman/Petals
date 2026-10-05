package br.com.colman.petals.use.io.input

import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.use.repository.ConsumptionMethod
import br.com.colman.petals.use.repository.Rating
import br.com.colman.petals.use.repository.Use
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.UUID.randomUUID

/**
 * One CSV row: the use, and the strain the row names, if any. [use] has no strain id yet, because the row's strain
 * may already be in the catalog under another id; [UseImporter] links them once [StrainResolver] knows.
 */
data class UseCsvRow(val use: Use, val strain: CsvStrain?)

/**
 * A strain as a CSV row describes it. Unlike a [Strain] it may have no [id], when the row gave none, so it can't
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
  private val Uuid = Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")

  /**
   * The rows of a whole file. A quoted value may span lines, as notes with a line break are exported, so a row can
   * take more than one line. Rows are read one by one rather than with [csvReader]'s readAll, which fails a file whose
   * rows don't all have as many values as the first, as an old export's header has fewer labels than its rows.
   */
  fun rowsOf(csv: String): List<List<String>> = csvReader.open(csv.byteInputStream()) {
    generateSequence { readNext() }.toList()
  }

  /**
   * Where the strain's columns start in a file with this [header], or null when it has none. They are found by their
   * fixed labels, in [Strain.CsvHeader] order, so a file from before strains, a file without a header, or columns a
   * user added in a spreadsheet are never read as a strain.
   */
  fun strainColumnsIn(header: List<String>): Int? = header
    .map { it.trim() }
    .windowed(Strain.CsvColumnCount)
    .indexOfFirst { it == Strain.CsvHeader }
    .takeIf { it >= 0 }

  /**
   * Where the rating is in a file with this [header], or null when it has none. It is found by its fixed label, like
   * the strain's columns, so a file from before ratings or a column a user added is never read as a rating.
   */
  fun ratingColumnIn(header: List<String>): Int? = header.map { it.trim() }.indexOf(Rating.CsvColumn).takeIf { it >= 0 }

  /**
   * Reads a row, its strain from the columns starting at [strainColumns] and its rating from [ratingColumn], if the
   * file has them. A rating is rounded to the nearest half star, and dropped when it can't be read or is out of range.
   */
  fun parse(
    values: List<String>,
    strainColumns: Int? = null,
    ratingColumn: Int? = null
  ): Result<UseCsvRow> = runCatching {
    val dateTime = parseDateTime(values[0])
    val amount = values[1].toBigDecimal()
    val cost = values[2].toBigDecimal()
    val id = parseOrGenerateUUID(values.getOrNull(3))
    val description = values.getOrElse(4) { "" }
    val consumptionMethod = ConsumptionMethod.fromKey(values.getOrElse(5) { "" })
    val strain = strainColumns?.let { parseStrain { label -> values.getOrNull(it + Strain.CsvHeader.indexOf(label)) } }

    val rating = ratingColumn?.let { values.getOrNull(it) }?.let(::parseRating)

    UseCsvRow(Use(dateTime, amount, cost, id, description, consumptionMethod, rating = rating), strain)
  }

  /**
   * A row names a strain only when it has a strain name: an id alone can't recreate the strain on another install.
   * Every strain value is optional, so one that can't be read is dropped rather than failing the whole row. Only a
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
      valueOf(Strain.CostColumn)?.let(Strain::costOrNull),
      valueOf(Strain.ArchivedColumn)?.trim().let { it.equals("true", ignoreCase = true) || it == "1" }
    )
  }

  private fun parsePercentage(value: String?) = value?.let(Strain::percentageOrNull)

  private fun parseRating(value: String) = value.trim().replace(',', '.').toDoubleOrNull()?.let(Rating::ofOrNull)

  private fun parseDateTime(date: String) = LocalDateTime.parse(date, ISO_LOCAL_DATE_TIME)

  private fun parseOrGenerateUUID(uuid: String?) = if (uuid.isNullOrBlank()) randomUUID().toString() else uuid
}
