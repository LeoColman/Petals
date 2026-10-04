package br.com.colman.petals.strain.repository

import java.math.BigDecimal
import java.text.Normalizer
import java.util.Locale
import java.util.UUID

/**
 * A strain from the catalog that uses can be logged with. [costPerGram] is only what the add-use form starts from:
 * every use keeps its own cost, so changing it here never rewrites past spending.
 */
data class Strain(
  val name: String,

  val thcPercent: BigDecimal? = null,

  val cbdPercent: BigDecimal? = null,

  val costPerGram: BigDecimal? = null,

  val isArchived: Boolean = false,

  val id: String = UUID.randomUUID().toString()
) {

  /** [name] in the form two names are compared in, see [nameKey]. */
  val nameKey: String by lazy { nameKey(name) }

  /**
   * The strain's CSV columns, written after the use's own on every exported line, in the order of [CsvHeader]. They
   * hold the whole strain, so a backup restores every strain a use was logged with as it was, archived flag and
   * default cost included. A strain no use was logged with has no line to ride on, so it isn't in the backup: every
   * line is a use, and a line that isn't would make older versions of the app reject the whole file.
   */
  fun columns(): List<String> = listOf(
    id,
    name,
    thcPercent?.toPlainString().orEmpty(),
    cbdPercent?.toPlainString().orEmpty(),
    costPerGram?.toPlainString().orEmpty(),
    isArchived.toString()
  )

  fun hasName(other: String): Boolean = nameKey == nameKey(other)

  companion object {
    const val IdColumn = "strain_id"
    const val NameColumn = "strain_name"
    const val ThcColumn = "strain_thc_percent"
    const val CbdColumn = "strain_cbd_percent"
    const val CostColumn = "strain_cost_per_gram"
    const val ArchivedColumn = "strain_archived"

    /**
     * The labels of [columns], in its order. Unlike the use's columns they are never translated: the importer finds
     * the strain's columns by these labels, so columns a user added to a file are never read as a strain.
     */
    val CsvHeader = listOf(IdColumn, NameColumn, ThcColumn, CbdColumn, CostColumn, ArchivedColumn)

    /** How many columns [columns] writes. */
    val CsvColumnCount = CsvHeader.size

    /**
     * Names count as the same strain regardless of case, surrounding spaces and how an accent was encoded, so
     * "Café Kush" typed on a phone matches the same name saved by a desktop tool as e + combining accent.
     *
     * Each letter is lowercased, uppercased and lowercased again, which folds what one mapping leaves apart:
     * "Straße", "STRASSE" and "STRAẞE". Turkish dotless "ı" is left alone, since it is not "i"; the dot a lowercased
     * "İ" keeps is dropped, so "İpek" matches "ipek". Normalizing comes last, because case mapping can itself
     * decompose a letter, as with Greek "ΐ".
     */
    fun nameKey(name: String): String {
      val folded = buildString {
        name.trim().lowercase(Locale.ROOT).codePoints().forEach { codePoint ->
          val letter = String(Character.toChars(codePoint))
          append(if (codePoint == DotlessI) letter else letter.uppercase(Locale.ROOT).lowercase(Locale.ROOT))
        }
      }
      return Normalizer.normalize(folded.replace("i̇", "i"), Normalizer.Form.NFC)
    }

    private const val DotlessI = 0x0131
  }
}
