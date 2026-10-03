package br.com.colman.petals.use.io.input

import br.com.colman.petals.strain.repository.Strain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.math.BigDecimal

class StrainResolverTest : FunSpec({

  val flm = Strain("420 Evo FLM", BigDecimal("27"), BigDecimal("1"), id = "flm")
  val bedrocan = Strain("Bedrocan", id = "bedrocan")

  test("Creates nothing before it is asked to resolve") {
    StrainResolver(listOf(flm)).created.shouldBeEmpty()
  }

  test("Uses the catalog strain with the same id, even under another name") {
    val renamed = flm.copy(name = "FLM, new batch")
    val target = StrainResolver(listOf(renamed))

    target.resolve(flm) shouldBeSameInstanceAs renamed
    target.created.shouldBeEmpty()
  }

  test("Prefers the strain with the same id over one with the same name") {
    val sameId = flm.copy(name = "Renamed")
    val sameName = Strain(flm.name, id = "another")
    val target = StrainResolver(listOf(sameName, sameId))

    target.resolve(flm) shouldBeSameInstanceAs sameId
  }

  test("Uses the catalog strain with the same name, ignoring case and spaces") {
    val catalogued = Strain(" 420 evo flm ", id = "catalogued")
    val target = StrainResolver(listOf(bedrocan, catalogued))

    target.resolve(flm) shouldBeSameInstanceAs catalogued
    target.created.shouldBeEmpty()
  }

  test("Matches a name however its accents were encoded") {
    val composed = Strain("Café Kush", id = "composed")
    val target = StrainResolver(listOf(composed))

    target.resolve(Strain("Café Kush", id = "decomposed")) shouldBeSameInstanceAs composed
  }

  test("Prefers an active strain to an archived one with the same name") {
    val archived = Strain(flm.name, isArchived = true, id = "old-batch")
    val active = Strain(flm.name, id = "new-batch")

    StrainResolver(listOf(active, archived)).resolve(flm.copy(id = "elsewhere")) shouldBeSameInstanceAs active
    StrainResolver(listOf(archived, active)).resolve(flm.copy(id = "elsewhere")) shouldBeSameInstanceAs active
  }

  test("Falls back to an archived strain when it is the only one with that name") {
    val archived = Strain(flm.name, isArchived = true, id = "old-batch")

    StrainResolver(listOf(archived)).resolve(flm) shouldBeSameInstanceAs archived
  }

  test("Creates a strain the catalog does not have, as the line described it") {
    val target = StrainResolver(listOf(bedrocan))

    target.resolve(flm) shouldBeSameInstanceAs flm
    target.created shouldContainExactly listOf(flm)
  }

  test("Creates a strain once, however many lines name it by its id") {
    val target = StrainResolver(emptyList())

    target.resolve(flm)
    target.resolve(flm.copy()) shouldBeSameInstanceAs flm

    target.created shouldContainExactly listOf(flm)
  }

  test("Keeps apart strains it created that share a name but not an id") {
    val oldBatch = Strain(flm.name, isArchived = true, id = "old-batch")
    val newBatch = Strain(flm.name, id = "new-batch")
    val target = StrainResolver(emptyList())

    target.resolve(oldBatch) shouldBeSameInstanceAs oldBatch
    target.resolve(newBatch) shouldBeSameInstanceAs newBatch
    target.created shouldContainExactly listOf(oldBatch, newBatch)
  }

  test("Lists the strains it created in the order it created them") {
    val target = StrainResolver(emptyList())

    target.resolve(bedrocan)
    target.resolve(flm)

    target.created shouldContainExactly listOf(bedrocan, flm)
  }
})
