package br.com.colman.petals.use

import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.use.repository.Use
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AddUseRequestTest : FunSpec({

  val active = Strain("420 Evo FLM", id = "flm")
  val archived = Strain("Old batch", isArchived = true, id = "old")
  val use = Use(description = "With a friend")

  test("A new use starts with the copied use's strain while it is active") {
    AddUseRequest.from(use.copy(strainId = active.id), active).template shouldBe use.copy(strainId = active.id)
  }

  test("A new use starts with no strain when the copied one is archived") {
    AddUseRequest.from(use.copy(strainId = archived.id), archived).template shouldBe use
  }

  test("A new use starts with no strain when the copied one is gone") {
    AddUseRequest.from(use.copy(strainId = "deleted"), null).template shouldBe use
  }

  test("A strain that isn't the copied use's is ignored") {
    AddUseRequest.from(use.copy(strainId = archived.id), active).template shouldBe use
  }

  test("Everything else is copied as it is") {
    val full = use.copy(strainId = active.id, description = "notes")

    AddUseRequest.from(full, active).template shouldBe full
  }

  test("Without a use to copy, the form starts blank") {
    AddUseRequest.from(null, active).template shouldBe null
  }
})
