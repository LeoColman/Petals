package br.com.colman.petals.use

import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.use.repository.Use
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AddUseRequestTest : FunSpec({

  val active = Strain("420 Evo FLM", id = "flm")
  val archived = Strain("Old batch", isArchived = true, id = "old")
  val use = Use(description = "With a friend")

  // A template is dated now, see Use.asTemplate, so these compare everything else.
  fun templateFrom(copied: Use, strain: Strain?) = AddUseRequest.from(copied, strain).template?.copy(date = copied.date)

  test("A new use starts with the copied use's strain while it is active") {
    templateFrom(use.copy(strainId = active.id), active) shouldBe use.copy(strainId = active.id)
  }

  test("A new use starts with no strain when the copied one is archived") {
    templateFrom(use.copy(strainId = archived.id), archived) shouldBe use
  }

  test("A new use starts with no strain when the copied one is gone") {
    templateFrom(use.copy(strainId = "deleted"), null) shouldBe use
  }

  test("A strain that isn't the copied use's is ignored") {
    templateFrom(use.copy(strainId = archived.id), active) shouldBe use
  }

  test("Everything else is copied as it is") {
    val full = use.copy(strainId = active.id, description = "notes")

    templateFrom(full, active) shouldBe full
  }

  test("A new use starts unrated, however the copied one was rated") {
    templateFrom(use.copy(strainId = active.id, rating = 4.5), active) shouldBe use.copy(strainId = active.id)
  }

  test("Without a use to copy, the form starts blank") {
    AddUseRequest.from(null, active).template shouldBe null
  }
})
