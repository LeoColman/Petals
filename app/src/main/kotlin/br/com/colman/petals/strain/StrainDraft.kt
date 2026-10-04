package br.com.colman.petals.strain

import br.com.colman.petals.strain.repository.Strain
import java.math.BigDecimal

/** What the strain form holds, as typed, and whether it makes a strain. */
data class StrainDraft(
  val name: String = "",
  val thcPercent: String = "",
  val cbdPercent: String = "",
  val costPerGram: String = ""
) {
  constructor(strain: Strain) : this(
    strain.name,
    strain.thcPercent?.toPlainString().orEmpty(),
    strain.cbdPercent?.toPlainString().orEmpty(),
    strain.costPerGram?.toPlainString().orEmpty()
  )

  /**
   * What stops this draft from becoming a strain. A name must be given, and no other strain in [catalog] may have it,
   * archived ones included, since names are how imports from another phone find their strain. [editing] is the
   * strain being changed, whose own name doesn't count as taken.
   */
  fun problems(catalog: List<Strain>, editing: Strain? = null): Set<StrainProblem> = buildSet {
    if (name.isBlank()) add(StrainProblem.NameMissing)
    if (catalog.any { it.id != editing?.id && it.hasName(name) }) add(StrainProblem.NameTaken)
    if (!thcPercent.isPercentage()) add(StrainProblem.ThcOutOfRange)
    if (!cbdPercent.isPercentage()) add(StrainProblem.CbdOutOfRange)
    if (!costPerGram.isCost()) add(StrainProblem.CostInvalid)
  }

  /** The strain this draft makes, keeping [editing]'s id and archived flag. Only meaningful without [problems]. */
  fun toStrain(editing: Strain? = null): Strain = (editing ?: Strain(name)).copy(
    name = name.trim(),
    thcPercent = thcPercent.toNumber(),
    cbdPercent = cbdPercent.toNumber(),
    costPerGram = costPerGram.toNumber()
  )
}

enum class StrainProblem { NameMissing, NameTaken, ThcOutOfRange, CbdOutOfRange, CostInvalid }

private val Percentages = BigDecimal.ZERO..BigDecimal(100)

// A decimal comma, as many keyboards type it, reads as a decimal point.
private fun String.toNumber() = trim().replace(',', '.').toBigDecimalOrNull()

private fun String.isPercentage() = isBlank() || toNumber()?.let { it in Percentages } == true

private fun String.isCost() = isBlank() || toNumber()?.let { it >= BigDecimal.ZERO } == true
