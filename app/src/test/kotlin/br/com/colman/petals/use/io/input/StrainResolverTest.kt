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

  test("Creates a strain the catalog does not have, as the line described it") {
    val target = StrainResolver(listOf(bedrocan))

    target.resolve(flm) shouldBeSameInstanceAs flm
    target.created shouldContainExactly listOf(flm)
  }

  test("Creates a strain once, however many lines name it") {
    val target = StrainResolver(emptyList())

    target.resolve(flm)
    target.resolve(flm) shouldBeSameInstanceAs flm
    target.resolve(flm.copy(id = "from-another-install")) shouldBeSameInstanceAs flm

    target.created shouldContainExactly listOf(flm)
  }

  test("Lists the strains it created in the order it created them") {
    val target = StrainResolver(emptyList())

    target.resolve(bedrocan)
    target.resolve(flm)

    target.created shouldContainExactly listOf(bedrocan, flm)
  }
})
