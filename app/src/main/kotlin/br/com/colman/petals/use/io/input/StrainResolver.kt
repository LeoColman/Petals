package br.com.colman.petals.use.io.input

import br.com.colman.petals.strain.repository.Strain
import java.util.UUID

/**
 * Matches the strains named in an import to the catalog.
 *
 * A line with a strain id is matched by that id first, so a backup restores onto the strains it came from even if
 * they were renamed since; then by name among strains already in the catalog, so another install's export joins
 * strains the user has; and is created with its id otherwise. It is never matched by name against strains this
 * import created: two strains a backup keeps apart by id stay apart.
 *
 * A line without an id has only its name, so it is matched by name in the catalog, then among strains this import
 * created, and is created with a new id otherwise. Every id-less line naming one strain therefore lands on it.
 *
 * Between strains with the same name, an active one wins over an archived one, and then the lowest id, so the
 * outcome never depends on the order the catalog was read in.
 */
class StrainResolver(catalog: List<Strain>) {
  private val byId = catalog.associateByTo(mutableMapOf()) { it.id }
  private val catalogByName = catalog.groupBy { it.nameKey }.mapValues { (_, same) -> same.minWith(Preferred) }
  private val createdByName = mutableMapOf<String, Strain>()
  private val _created = mutableListOf<Strain>()

  /** The strains [resolve] had to create, in the order it created them. */
  val created: List<Strain> get() = _created

  fun resolve(strain: Strain): Strain {
    if (strain.id.isNotEmpty()) {
      return byId[strain.id] ?: catalogByName[strain.nameKey] ?: create(strain)
    }
    return catalogByName[strain.nameKey]
      ?: createdByName[strain.nameKey]
      ?: create(strain.copy(id = UUID.randomUUID().toString()))
  }

  private fun create(strain: Strain): Strain = strain.also {
    byId[it.id] = it
    createdByName.putIfAbsent(it.nameKey, it)
    _created += it
  }
}

private val Preferred = compareBy<Strain> { it.isArchived }.thenBy { it.id }
