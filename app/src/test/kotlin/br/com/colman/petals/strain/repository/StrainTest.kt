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
    test("Writes the whole strain: id, name, potencies, default cost and whether it is archived") {
      val strain = Strain("420 Evo FLM", BigDecimal("27.5"), BigDecimal("1"), BigDecimal("12.50"), true, "flm")

      strain.columns() shouldBe listOf("flm", "420 Evo FLM", "27.5", "1", "12.50", "true")
    }

    test("Writes a missing potency or cost as an empty column, and an active strain as not archived") {
      Strain("Bedrocan", id = "bed").columns() shouldBe listOf("bed", "Bedrocan", "", "", "", "false")
    }

    test("Writes potencies as plain numbers, never in scientific notation") {
      val strain = Strain("Tiny", BigDecimal("1E-1"), BigDecimal("2E+1"), id = "t")

      strain.columns() shouldBe listOf("t", "Tiny", "0.1", "20", "", "false")
    }
  }

  test("CsvColumnCount is how many columns a strain writes") {
    Strain("Bedrocan").columns().size shouldBe Strain.CsvColumnCount
  }

  context("nameKey") {
    test("Is the trimmed, composed, lowercase name") {
      Strain("  Cafe\u0301 KUSH ").nameKey shouldBe "caf\u00e9 kush"
    }

    test("Is the same for the instance and the companion") {
      Strain(" FLM ").nameKey shouldBe Strain.nameKey("flm")
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

    test("Ignores how an accent was encoded") {
      Strain("Caf\u00e9 Kush").hasName("Cafe\u0301 Kush").shouldBeTrue()
    }

    test("Folds case beyond what lower case alone does") {
      Strain("Stra\u00dfe Kush").hasName("STRASSE KUSH").shouldBeTrue()
    }

    test("Matches a Greek name whichever sigma ends it") {
      Strain("\u03c4\u03b1\u03c3").hasName("\u03c4\u03b1\u03c2").shouldBeTrue()
    }

    test("Matches a Greek name whose case mapping decomposes a letter") {
      Strain("\u0390").hasName("\u03aa\u0301").shouldBeTrue()
    }

    test("Matches a Turkish dotted capital I with its lowercase spelling") {
      Strain("\u0130stanbul").hasName("i\u0307stanbul").shouldBeTrue()
    }

    test("Folds the capital sharp S too") {
      Strain("STRA\u1e9eE KUSH").hasName("Stra\u00dfe Kush").shouldBeTrue()
      Strain("STRA\u1e9eE KUSH").hasName("STRASSE KUSH").shouldBeTrue()
    }

    test("Matches a Turkish dotted capital I however it is cased") {
      Strain("\u0130pek").hasName("ipek").shouldBeTrue()
      Strain("\u0130PEK").hasName("IPEK").shouldBeTrue()
    }

    test("Keeps Turkish dotless i apart from i") {
      Strain("K\u0131rm\u0131z\u0131").hasName("Kirmizi").shouldBeFalse()
      Strain("K\u0131rm\u0131z\u0131").hasName("K\u0131RM\u0131Z\u0131").shouldBeTrue()
    }

    test("Does not match a different name") {
      strain.hasName("420 Evo FLM 2").shouldBeFalse()
    }

    test("Keeps spaces inside the name significant") {
      strain.hasName("420 EvoFLM").shouldBeFalse()
    }
  }
})
