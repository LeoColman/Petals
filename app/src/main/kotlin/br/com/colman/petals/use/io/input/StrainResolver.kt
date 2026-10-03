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
 * created, and is created with a new id otherwise. Every id-less line naming one strain therefore lands on it. Use
 * [resolveAll] for a whole file, so strains with an id are created before any id-less line looks for them.
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

  /**
   * The strains of a whole file, each resolved in its place. Those with an id go first, so an id-less line read
   * before the line giving its strain's id joins that strain instead of creating a duplicate ahead of it.
   */
  fun resolveAll(strains: List<Strain?>): List<Strain?> {
    val resolved = arrayOfNulls<Strain>(strains.size)
    strains.withIndex()
      .sortedBy { (_, strain) -> strain?.id.isNullOrEmpty() }
      .forEach { (index, strain) -> resolved[index] = strain?.let(::resolve) }
    return resolved.toList()
  }

  fun resolve(strain: Strain): Strain {
    val key = strain.nameKey
    if (strain.id.isNotEmpty()) {
      return byId[strain.id] ?: catalogByName[key] ?: create(strain, key)
    }
    return catalogByName[key] ?: createdByName[key] ?: create(strain.copy(id = UUID.randomUUID().toString()), key)
  }

  private fun create(strain: Strain, key: String): Strain = strain.also {
    byId[it.id] = it
    createdByName.merge(key, it) { kept, new -> minOf(kept, new, Preferred) }
    _created += it
  }
}

private val Preferred = compareBy<Strain> { it.isArchived }.thenBy { it.id }
