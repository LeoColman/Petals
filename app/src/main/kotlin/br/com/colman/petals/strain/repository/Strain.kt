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
  val nameKey: String get() = nameKey(name)

  /**
   * The strain's CSV columns, written after the use's own on every exported line. They hold the whole strain, so a
   * backup restores the catalog as it was, archived strains and default costs included.
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
    /**
     * Names count as the same strain regardless of case, surrounding spaces and how an accent was encoded, so
     * "Café Kush" typed on a phone matches the same name saved by a desktop tool as e + combining accent. Going
     * through upper case first folds what lower case alone leaves apart, like "Straße" and "STRASSE". Normalizing
     * comes last, because case mapping can itself decompose a letter, as with Greek "ΐ".
     */
    fun nameKey(name: String): String =
      Normalizer.normalize(name.trim().uppercase(Locale.ROOT).lowercase(Locale.ROOT), Normalizer.Form.NFC)
  }
}
