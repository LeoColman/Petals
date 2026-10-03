package br.com.colman.petals.use.io.input

import br.com.colman.petals.strain.repository.Strain
import java.util.UUID

/**
 * Matches the strains an import names to the catalog.
 *
 * A strain with an id is matched by that id first, so a backup restores onto the strains it came from even if they
 * were renamed since; then by name to an active catalog strain, so another install's export joins the strains the
 * user is using, not ones they retired; and is created with its id otherwise. It is never matched by name to a
 * strain this import created, so two strains a backup keeps apart by id stay apart. Whatever a file id lands on,
 * every other line with that id lands there too.
 *
 * A strain without an id has only its name. It is matched first to whatever strain the file's own id-bearing lines
 * called by that name, even one since renamed in the catalog; then by name in the catalog, active strains first; and
 * is created with a new id otherwise. Every id-less line naming one strain lands on the same one.
 *
 * Between strains with the same name, an active one wins, and then the lowest id, so the order the catalog or the
 * file came in never changes the outcome.
 */
class StrainResolver(catalog: List<Strain>) {
  private val byId = catalog.associateByTo(mutableMapOf()) { it.id }
  private val catalogByName = catalog.preferredByName()
  private val activeByName = catalog.filterNot { it.isArchived }.preferredByName()
  private val fileByName = mutableMapOf<String, Strain>()
  private val _created = mutableListOf<Strain>()

  /** The strains [resolve] had to create, in the order it created them. */
  val created: List<Strain> get() = _created

  /**
   * The strains of a whole file, each resolved in its place. Those with an id go first, so an id-less line read
   * before the line giving its strain's id joins that strain instead of creating a duplicate ahead of it.
   */
  fun resolveAll(strains: List<CsvStrain?>): List<Strain?> {
    val resolved = arrayOfNulls<Strain>(strains.size)
    strains.withIndex()
      .sortedBy { (_, strain) -> strain?.id == null }
      .forEach { (index, strain) -> resolved[index] = strain?.let(::resolve) }
    return resolved.toList()
  }

  fun resolve(strain: CsvStrain): Strain {
    val key = strain.nameKey
    val id = strain.id ?: return fileByName[key]
      ?: catalogByName[key]
      ?: create(strain.toStrain(UUID.randomUUID().toString())).also { fileByName[key] = it }

    return (byId[id] ?: activeByName[key] ?: create(strain.toStrain(id))).also { resolved ->
      byId[id] = resolved
      fileByName.merge(key, resolved) { kept, new -> minOf(kept, new, Preferred) }
    }
  }

  private fun create(strain: Strain): Strain = strain.also {
    byId[it.id] = it
    _created += it
  }
}

private val Preferred = compareBy<Strain> { it.isArchived }.thenBy { it.id }

private fun List<Strain>.preferredByName() = groupBy { it.nameKey }.mapValues { (_, same) -> same.minWith(Preferred) }
