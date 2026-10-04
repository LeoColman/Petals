package br.com.colman.petals.strain

import br.com.colman.petals.strain.StrainProblem.CbdOutOfRange
import br.com.colman.petals.strain.StrainProblem.CostInvalid
import br.com.colman.petals.strain.StrainProblem.NameMissing
import br.com.colman.petals.strain.StrainProblem.NameTaken
import br.com.colman.petals.strain.StrainProblem.ThcOutOfRange
import br.com.colman.petals.strain.repository.Strain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeUUID
import java.math.BigDecimal

class StrainDraftTest : FunSpec({

  val flm = Strain("420 Evo FLM", BigDecimal("27"), BigDecimal("1"), BigDecimal("12.50"), id = "flm")
  val catalog = listOf(flm, Strain("Old batch", isArchived = true, id = "old"))

  test("A draft with a new name and readable numbers has no problems") {
    StrainDraft("Bedrocan", "22", "1", "9.80").problems(catalog).shouldBeEmpty()
  }

  test("Numbers may be left blank") {
    StrainDraft("Bedrocan").problems(catalog).shouldBeEmpty()
  }

  context("Name") {
    test("must be given") {
      StrainDraft("  ").problems(catalog) shouldContainExactly setOf(NameMissing)
    }

    test("can't be another strain's, ignoring case and spaces") {
      StrainDraft(" 420 evo flm ").problems(catalog) shouldContainExactly setOf(NameTaken)
    }

    test("can't be an archived strain's either") {
      StrainDraft("old batch").problems(catalog) shouldContainExactly setOf(NameTaken)
    }

    test("can stay the same when editing that strain") {
      StrainDraft(flm).problems(catalog, editing = flm).shouldBeEmpty()
    }
  }

  context("Potency") {
    test("must be from 0 to 100") {
      StrainDraft("Bedrocan", "101", "-1").problems(catalog) shouldContainExactly setOf(ThcOutOfRange, CbdOutOfRange)
      StrainDraft("Bedrocan", "100", "0").problems(catalog).shouldBeEmpty()
    }

    test("must be a number") {
      StrainDraft("Bedrocan", "lots").problems(catalog) shouldContainExactly setOf(ThcOutOfRange)
    }
  }

  context("Cost") {
    test("can't be negative or unreadable") {
      StrainDraft("Bedrocan", costPerGram = "-1").problems(catalog) shouldContainExactly setOf(CostInvalid)
      StrainDraft("Bedrocan", costPerGram = "cheap").problems(catalog) shouldContainExactly setOf(CostInvalid)
    }

    test("can be 0") {
      StrainDraft("Bedrocan", costPerGram = "0").problems(catalog).shouldBeEmpty()
    }
  }

  context("toStrain") {
    test("makes a new active strain with a new id, the name trimmed and blanks as none") {
      val strain = StrainDraft("  Bedrocan ", "22").toStrain()

      strain.id.shouldBeUUID()
      strain shouldBe Strain("Bedrocan", BigDecimal("22"), id = strain.id)
    }

    test("reads a decimal comma as a decimal point, as many keyboards type it") {
      StrainDraft("Bedrocan", "22,5", "0,5", "9,80").toStrain().let {
        it.thcPercent shouldBe BigDecimal("22.5")
        it.cbdPercent shouldBe BigDecimal("0.5")
        it.costPerGram shouldBe BigDecimal("9.80")
      }
      StrainDraft("Bedrocan", "22,5").problems(catalog).shouldBeEmpty()
    }

    test("keeps the edited strain's id and archived flag") {
      val archived = flm.copy(isArchived = true)

      StrainDraft("FLM, new batch", "28").toStrain(editing = archived) shouldBe
        archived.copy(name = "FLM, new batch", thcPercent = BigDecimal("28"), cbdPercent = null, costPerGram = null)
    }

    test("round trips a strain through the form unchanged") {
      StrainDraft(flm).toStrain(editing = flm) shouldBe flm
    }
  }
})
