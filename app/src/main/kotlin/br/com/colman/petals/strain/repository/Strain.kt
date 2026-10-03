package br.com.colman.petals.strain.repository

import java.math.BigDecimal
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

  /** The strain's CSV columns, written after the use's own on every exported line. */
  fun columns(): List<String> = listOf(
    id,
    name,
    thcPercent?.toPlainString().orEmpty(),
    cbdPercent?.toPlainString().orEmpty()
  )

  /** Names count as the same strain regardless of case and surrounding spaces, in any script. */
  fun hasName(other: String): Boolean = name.trim().equals(other.trim(), ignoreCase = true)
}
