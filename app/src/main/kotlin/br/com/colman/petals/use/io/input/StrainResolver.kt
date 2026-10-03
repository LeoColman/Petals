package br.com.colman.petals.use.io.input

import br.com.colman.petals.strain.repository.Strain

/**
 * Matches the strains named in an import to the catalog. The same id comes first, so a backup restores onto the
 * strains it came from even if they were renamed since. Then the same name, among strains that were already in the
 * catalog only, so another install's export joins strains the user has, preferring an active one to an archived one.
 * Only then is a new strain created. Names are not matched against strains this import created: two strains a backup
 * keeps apart by id, like an archived batch and a new one with the same name, stay apart.
 */
class StrainResolver(catalog: List<Strain>) {
  private val byId = catalog.associateByTo(mutableMapOf()) { it.id }
  private val byName = catalog.sortedByDescending { it.isArchived }.associateBy { it.nameKey }
  private val _created = mutableListOf<Strain>()

  /** The strains [resolve] had to create, in the order it created them. */
  val created: List<Strain> get() = _created

  fun resolve(strain: Strain): Strain =
    byId[strain.id]
      ?: byName[strain.nameKey]
      ?: strain.also {
        byId[it.id] = it
        _created += it
      }
}
