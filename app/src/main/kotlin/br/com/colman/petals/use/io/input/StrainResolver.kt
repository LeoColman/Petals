package br.com.colman.petals.use.io.input

import br.com.colman.petals.strain.repository.Strain

/**
 * Matches the strains named in an import to the catalog: the same id first, so a backup restores onto the strains it
 * came from even if they were renamed since; then the same name, so another install's export joins strains the user
 * already has; and only then a new strain. A strain created here is matched by later lines too, so one that appears
 * on many lines is created once.
 */
class StrainResolver(catalog: List<Strain>) {
  private val known = catalog.toMutableList()
  private val _created = mutableListOf<Strain>()

  /** The strains [resolve] had to create, in the order it created them. */
  val created: List<Strain> get() = _created

  fun resolve(strain: Strain): Strain =
    known.firstOrNull { it.id == strain.id }
      ?: known.firstOrNull { it.hasName(strain.name) }
      ?: strain.also {
        known += it
        _created += it
      }
}
