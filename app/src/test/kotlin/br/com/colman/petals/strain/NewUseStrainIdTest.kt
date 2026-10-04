package br.com.colman.petals.strain

import br.com.colman.petals.strain.repository.Strain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NewUseStrainIdTest : FunSpec({

  val active = Strain("420 Evo FLM", id = "flm")
  val archived = Strain("Old batch", isArchived = true, id = "old")
  val catalog = listOf(active, archived)

  test("A new use starts with the copied strain when it is active") {
    newUseStrainId("flm", catalog) shouldBe "flm"
  }

  test("A new use starts with no strain when the copied one is archived") {
    newUseStrainId("old", catalog) shouldBe null
  }

  test("A new use starts with no strain when the copied one is gone") {
    newUseStrainId("deleted", catalog) shouldBe null
  }

  test("A new use copied from one without a strain has none") {
    newUseStrainId(null, catalog) shouldBe null
  }
})
