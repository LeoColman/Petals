package br.com.colman.petals.use.io.input

import br.com.colman.petals.strain.repository.Strain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeUUID
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.math.BigDecimal

class StrainResolverTest : FunSpec({

  val flm = Strain("420 Evo FLM", BigDecimal("27"), BigDecimal("1"), id = "flm")
  val bedrocan = Strain("Bedrocan", id = "bedrocan")

  fun Strain.inFile(id: String? = this.id, name: String = this.name) =
    CsvStrain(id, name, thcPercent, cbdPercent, costPerGram, isArchived)

  test("Creates nothing before it is asked to resolve") {
    StrainResolver(listOf(flm)).created.shouldBeEmpty()
  }

  context("A strain with an id") {
    test("is matched by that id, even under another name") {
      val renamed = flm.copy(name = "FLM, new batch")
      val target = StrainResolver(listOf(renamed))

      target.resolve(flm.inFile()) shouldBeSameInstanceAs renamed
      target.created.shouldBeEmpty()
    }

    test("is matched by id before name") {
      val sameId = flm.copy(name = "Renamed")
      val sameName = Strain(flm.name, id = "another")

      StrainResolver(listOf(sameName, sameId)).resolve(flm.inFile()) shouldBeSameInstanceAs sameId
    }

    test("is otherwise matched by name to an active catalog strain, ignoring case and spaces") {
      val catalogued = Strain(" 420 evo flm ", id = "catalogued")
      val target = StrainResolver(listOf(bedrocan, catalogued))

      target.resolve(flm.inFile(id = "elsewhere")) shouldBeSameInstanceAs catalogued
      target.created.shouldBeEmpty()
    }

    test("is matched by name however its accents were encoded") {
      val composed = Strain("Café Kush", id = "composed")
      val decomposed = composed.inFile(id = "elsewhere", name = "Café Kush")

      StrainResolver(listOf(composed)).resolve(decomposed) shouldBeSameInstanceAs composed
    }

    test("is not matched by name to an archived strain, so it doesn't land on a retired batch") {
      val archived = Strain(flm.name, isArchived = true, id = "old-batch")
      val target = StrainResolver(listOf(archived))

      target.resolve(flm.inFile()) shouldBe flm
      target.created shouldContainExactly listOf(flm)
    }

    test("is created as the line described it when nothing matches") {
      val target = StrainResolver(listOf(bedrocan))

      target.resolve(flm.inFile()) shouldBe flm
      target.created shouldContainExactly listOf(flm)
    }

    test("is created once, however many lines give its id") {
      val target = StrainResolver(emptyList())

      val created = target.resolve(flm.inFile())
      target.resolve(flm.inFile()) shouldBeSameInstanceAs created

      target.created shouldContainExactly listOf(flm)
    }

    test("lands every line with that id where the first landed, even under another name") {
      val catalogued = Strain("A", id = "catalogued")
      val target = StrainResolver(listOf(catalogued))

      target.resolve(CsvStrain("file-id", "A")) shouldBeSameInstanceAs catalogued
      target.resolve(CsvStrain("file-id", "B")) shouldBeSameInstanceAs catalogued
      target.created.shouldBeEmpty()
    }

    test("is kept apart from a created strain that shares its name but not its id") {
      val oldBatch = Strain(flm.name, isArchived = true, id = "old-batch")
      val newBatch = Strain(flm.name, id = "new-batch")
      val target = StrainResolver(emptyList())

      target.resolve(oldBatch.inFile())
      target.resolve(newBatch.inFile())

      target.created shouldContainExactly listOf(oldBatch, newBatch)
    }
  }

  context("A strain without an id") {
    val idLess = bedrocan.inFile(id = null)

    test("is matched by name in the catalog, active strains first") {
      val archived = Strain("Bedrocan", isArchived = true, id = "a-archived")
      val active = Strain("bedrocan", id = "z-active")

      StrainResolver(listOf(archived, active)).resolve(idLess) shouldBeSameInstanceAs active
      StrainResolver(listOf(archived)).resolve(idLess) shouldBeSameInstanceAs archived
    }

    test("is created with a new id when nothing has its name") {
      val target = StrainResolver(emptyList())

      val created = target.resolve(idLess)

      created.id.shouldBeUUID()
      created shouldBe idLess.toStrain(created.id)
      target.created shouldContainExactly listOf(created)
    }

    test("lands on one strain however many lines name it") {
      val target = StrainResolver(emptyList())

      val created = target.resolve(idLess)
      target.resolve(idLess.copy(name = " BEDROCAN ")) shouldBeSameInstanceAs created

      target.created shouldContainExactly listOf(created)
    }

    test("joins the strain the file's id-bearing lines call by that name, even one renamed in the catalog") {
      val renamed = bedrocan.copy(name = "Bedrocan, 2023 batch")
      val target = StrainResolver(listOf(renamed))

      target.resolve(bedrocan.inFile()) shouldBeSameInstanceAs renamed
      target.resolve(idLess) shouldBeSameInstanceAs renamed
      target.created.shouldBeEmpty()
    }

    test("prefers the file's own strain over a catalog strain with that name") {
      val archived = Strain("Bedrocan", isArchived = true, id = "old-batch")
      val target = StrainResolver(listOf(archived))

      val fromFile = target.resolve(bedrocan.inFile())

      target.resolve(idLess) shouldBeSameInstanceAs fromFile
    }
  }

  context("Ties between strains with the same name") {
    test("go to the active one, then the lowest id, whatever order the catalog came in") {
      val first = Strain(flm.name, id = "a")
      val second = Strain(flm.name, id = "b")

      StrainResolver(listOf(first, second)).resolve(flm.inFile(id = "x")) shouldBeSameInstanceAs first
      StrainResolver(listOf(second, first)).resolve(flm.inFile(id = "x")) shouldBeSameInstanceAs first
    }

    test("among the file's own strains go the same way, whatever order the lines came in") {
      val b = Strain("Bedrocan", id = "b")
      val a = Strain("Bedrocan", id = "a")
      val idLess = bedrocan.inFile(id = null)

      StrainResolver(emptyList()).resolveAll(listOf(b.inFile(), a.inFile(), idLess)).last() shouldBe a
      StrainResolver(emptyList()).resolveAll(listOf(a.inFile(), b.inFile(), idLess)).last() shouldBe a
    }
  }

  context("resolveAll") {
    test("keeps each strain in its line's place, with no strain where a line had none") {
      val target = StrainResolver(listOf(bedrocan))

      target.resolveAll(listOf(flm.inFile(), null, bedrocan.inFile(id = null))) shouldBe listOf(flm, null, bedrocan)
    }

    test("joins an id-less line to the strain a later line gives the id of, instead of making two") {
      val withId = Strain("Bedrocan", id = "from-the-file")
      val target = StrainResolver(emptyList())

      val resolved = target.resolveAll(listOf(withId.inFile(id = null, name = "bedrocan"), withId.inFile()))

      resolved shouldBe listOf(withId, withId)
      target.created shouldContainExactly listOf(withId)
    }
  }

  test("Lists the strains it created in the order it created them") {
    val target = StrainResolver(emptyList())

    target.resolve(bedrocan.inFile())
    target.resolve(flm.inFile())

    target.created shouldContainExactly listOf(bedrocan, flm)
  }
})
