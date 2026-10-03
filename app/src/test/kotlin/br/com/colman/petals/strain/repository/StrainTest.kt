package br.com.colman.petals.strain.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeUUID
import java.math.BigDecimal

class StrainTest : FunSpec({

  test("A new strain gets a random id and starts unarchived, without potency or cost") {
    val strain = Strain("420 Evo FLM")

    strain.id.shouldBeUUID()
    strain.isArchived.shouldBeFalse()
    strain.thcPercent shouldBe null
    strain.cbdPercent shouldBe null
    strain.costPerGram shouldBe null
  }

  context("columns()") {
    test("Writes the id, name and potencies, without the cost or archived flag") {
      val strain = Strain("420 Evo FLM", BigDecimal("27.5"), BigDecimal("1"), BigDecimal("12.50"), true, "flm")

      strain.columns() shouldBe listOf("flm", "420 Evo FLM", "27.5", "1")
    }

    test("Writes a missing potency as an empty column") {
      Strain("Bedrocan", id = "bed").columns() shouldBe listOf("bed", "Bedrocan", "", "")
    }

    test("Writes potencies as plain numbers, never in scientific notation") {
      val strain = Strain("Tiny", BigDecimal("1E-1"), BigDecimal("2E+1"), id = "t")

      strain.columns() shouldBe listOf("t", "Tiny", "0.1", "20")
    }
  }

  context("hasName()") {
    val strain = Strain("420 Evo FLM")

    test("Matches the same name") {
      strain.hasName("420 Evo FLM").shouldBeTrue()
    }

    test("Ignores case") {
      strain.hasName("420 evo flm").shouldBeTrue()
    }

    test("Ignores spaces around either name") {
      Strain("  420 Evo FLM ").hasName(" 420 Evo FLM  ").shouldBeTrue()
    }

    test("Ignores case beyond ASCII") {
      Strain("Ärger Kush").hasName("ärger kush").shouldBeTrue()
    }

    test("Does not match a different name") {
      strain.hasName("420 Evo FLM 2").shouldBeFalse()
    }

    test("Keeps spaces inside the name significant") {
      strain.hasName("420 EvoFLM").shouldBeFalse()
    }
  }
})
