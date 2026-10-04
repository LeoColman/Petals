package br.com.colman.petals.strain

import br.com.colman.petals.strain.repository.Strain

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
    thcPercent = Strain.percentageOrNull(thcPercent),
    cbdPercent = Strain.percentageOrNull(cbdPercent),
    costPerGram = Strain.costOrNull(costPerGram)
  )
}

enum class StrainProblem { NameMissing, NameTaken, ThcOutOfRange, CbdOutOfRange, CostInvalid }

private fun String.isPercentage() = isBlank() || Strain.percentageOrNull(this) != null

private fun String.isCost() = isBlank() || Strain.costOrNull(this) != null
