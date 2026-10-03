package br.com.colman.petals.use.io.input

import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.use.UseArb
import br.com.colman.petals.use.io.UseCsvArb
import br.com.colman.petals.use.repository.ConsumptionMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeUUID
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.take
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class UseCsvParserTest : FunSpec({
  test("Converts CSV to Use") {
    val use = UseArb.next()
    val useCsv = use.columns().joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow().use shouldBe use
  }

  test("Returns failure if an invalid line is passed") {
    val useCsv = "invalid,cs,v"
    UseCsvParser.parse(useCsv).shouldBeFailure()
  }

  test("Returns failure if an empty line is passed") {
    val useCsv = ""
    UseCsvParser.parse(useCsv).shouldBeFailure()
  }

  test("Returns failure if fewer than three fields are passed") {
    val useCsv = "2024-02-09T12:00:00,100.00"
    UseCsvParser.parse(useCsv).shouldBeFailure()
  }

  test("Parses the id field if it's present") {
    val use = UseArb.next()
    val useCsv = use.columns().joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow().use.id shouldBe use.id
  }

  test("Creates new id if id field is empty") {
    val use = UseArb.next().copy(id = "")
    val useCsv = use.columns().joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow().use.id.shouldBeUUID()
  }

  test("Creates new id if id field is not present") {
    val use = UseArb.next().copy(id = "")
    val useCsv = use.columns().dropLast(1).joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow().use.id.shouldBeUUID()
  }

  test("Parsers the description field if present") {
    val use = UseArb.next().copy(description = "my description")
    val useCsv = use.columns().joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow().use.description shouldBe "my description"
  }

  test("Parsers the description field to empty if absent") {
    val use = UseArb.next().copy(description = "my description")
    val useCsv = use.columns().dropLast(2).joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow().use.description shouldBe ""
  }

  test("Parses the consumption method field if present") {
    val use = UseArb.next().copy(consumptionMethod = ConsumptionMethod.VAPORIZED)
    val useCsv = use.columns().joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow().use.consumptionMethod shouldBe ConsumptionMethod.VAPORIZED
  }

  test("Parses the consumption method field to null if absent (legacy 5-column CSV)") {
    val use = UseArb.next().copy(consumptionMethod = ConsumptionMethod.SMOKED)
    val legacyCsv = use.columns().dropLast(1).joinToString(",")

    legacyCsv.split(",") shouldHaveSize 5

    val parsed = UseCsvParser.parse(legacyCsv)

    parsed.getOrThrow().use.consumptionMethod shouldBe null
  }

  test("Parses the consumption method field to null if the key is unknown") {
    val use = UseArb.next()
    val csvWithUnknownMethod = use.columns().dropLast(1).plus("unknown-method").joinToString(",")
    val parsed = UseCsvParser.parse(csvWithUnknownMethod)

    parsed.getOrThrow().use.consumptionMethod shouldBe null
  }

  test("Parses successfully even if extra fields are present") {
    val use = UseArb.next()
    val extraField = "extra"
    val useCsv = use.columns().plus(extraField).joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow().use shouldBe use
  }

  test("Returns failure if date is not in ISO_LOCAL_DATE_TIME format") {
    val useCsv = "09/02/2024 12:00:00,100.00,50.00"
    UseCsvParser.parse(useCsv).shouldBeFailure()
  }

  test("Returns failure if date includes time zone information") {
    val useCsv = "2023-10-16T12:00:00Z,100.00,50.00"
    UseCsvParser.parse(useCsv).shouldBeFailure()
  }

  test("Returns failure if amount is not a valid number") {
    val useCsv = "2023-10-16T12:00:00,invalidAmount,50.00"
    UseCsvParser.parse(useCsv).shouldBeFailure()
  }

  test("Returns failure if amount or cost uses an exponent so far out that it can't be stored") {
    val use = UseArb.next()
    val columns = use.columns()

    UseCsvParser.parse((listOf(columns[0], "1E-3000000") + columns.drop(2)).joinToString(",")).shouldBeFailure()
    UseCsvParser.parse((columns.take(2) + "1E+3000000" + columns.drop(3)).joinToString(",")).shouldBeFailure()
  }

  test("Reads amount and cost written as .5, +2 or with a small exponent, as other tools write them") {
    val columns = UseArb.next().columns()
    val parsed = UseCsvParser.parse((columns.take(1) + listOf(".5", "1.25e1") + columns.drop(3)).joinToString(","))
      .getOrThrow().use

    parsed.amountGrams shouldBe BigDecimal(".5")
    parsed.costPerGram shouldBe BigDecimal("12.5")
    UseCsvParser.parse((columns.take(1) + listOf("+2", "3.") + columns.drop(3)).joinToString(",")).shouldBeSuccess()
  }

  test("Returns failure if cost is not a valid number") {
    val useCsv = "2023-10-16T12:00:00,100.00,invalidCost"
    UseCsvParser.parse(useCsv).shouldBeFailure()
  }

  test("Fails to parse with leading/trailing whitespaces in fields") {
    val use = UseArb.next()
    val useCsv = use.columns().joinToString(",") { " ${it.trim()} " }
    val parsed = UseCsvParser.parse(useCsv)

    parsed.shouldBeFailure()
  }

  test("Parses successfully with negative amount and cost values") {
    val use = UseArb.filter { it.amountGrams <= BigDecimal.ZERO || it.costPerGram <= BigDecimal.ZERO }.next()
    val useCsv = use.columns().joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow().use shouldBe use
  }

  test("Parses successfully when amount and cost are zero") {
    val use = UseArb.next().copy(amountGrams = BigDecimal.ZERO, costPerGram = BigDecimal.ZERO)
    val useCsv = use.columns().joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow().use shouldBe use
  }

  test("Parses successfully with very large amount and cost values") {
    val largeNumber = BigDecimal("9999999999999999999999999999.99")
    val use = UseArb.next().copy(amountGrams = largeNumber, costPerGram = largeNumber)
    val useCsv = use.columns().joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow().use shouldBe use
  }

  test("Returns failure if amount and cost have locale-specific formatting") {
    val useCsv = "2024-02-09T12:00:00,1.000,\"50,00\""
    UseCsvParser.parse(useCsv).shouldBeFailure()
  }

  test("Returns failure if date is logically invalid") {
    val useCsv = "2024-02-30T12:00:00,100.00,50.00" // February 30th doesn't exist
    UseCsvParser.parse(useCsv).shouldBeFailure()
  }

  test("Parses successfully when fields contain non-ASCII characters") {
    val use = UseArb.next()
    val dateWithUnicode = use.date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "𝓤"
    val useCsv = listOf(dateWithUnicode, use.amountGrams.toString(), use.costPerGram.toString()).joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.shouldBeFailure()
  }

  test("Returns failure if more than one line is passed") {
    val uses = UseCsvArb.take(Random.nextInt(2, 1000)).toList().joinToString("\n")
    UseCsvParser.parse(uses).shouldBeFailure()
  }

  context("strain columns") {
    val strain = Strain("420 Evo FLM", BigDecimal("27.5"), BigDecimal("1"))
    val parsedStrain = CsvStrain(strain.id, strain.name, strain.thcPercent, strain.cbdPercent, isArchived = false)
    fun line(vararg strainColumns: String) = (UseArb.next().columns() + strainColumns).joinToString(",")
    fun strainOf(line: String) = UseCsvParser.parse(line, strainColumns = 6).getOrThrow().strain

    test("A line from before strains, without strain columns, has no strain") {
      strainOf(line()) shouldBe null
    }

    test("A line with empty strain columns has no strain") {
      strainOf(line(*Array(Strain.CsvColumnCount) { "" })) shouldBe null
    }

    test("Reads the strain the line names, and leaves the use without a strain id for the importer to link") {
      val use = UseArb.next()
      val row = UseCsvParser.parse((use.columns() + strain.columns()).joinToString(","), 6).getOrThrow()

      row.strain shouldBe parsedStrain
      row.use shouldBe use
      row.use.strainId shouldBe null
    }

    test("Reads every column Strain writes back") {
      val full = strain.copy(costPerGram = BigDecimal("12.50"), isArchived = true)

      strainOf(line(*full.columns().toTypedArray()))!!.toStrain(full.id) shouldBe full
    }

    test("A strain id without a name is not a strain") {
      strainOf(line(strain.id, "", "27", "1")) shouldBe null
    }

    test("A strain without an id has none, for the importer to match by name") {
      strainOf(line(" ", strain.name))!!.id shouldBe null
    }

    test("Only a UUID counts as a strain id, so a hand-made id like 1 is matched by name instead") {
      strainOf(line("1", strain.name))!!.id shouldBe null
      strainOf(line("not-a-uuid", strain.name))!!.id shouldBe null
      strainOf(line(strain.id.uppercase(), strain.name))!!.id shouldBe strain.id.uppercase()
    }

    test("Is never read from a file whose header doesn't have the strain columns") {
      val use = UseArb.next()
      val line = (use.columns() + strain.columns()).joinToString(",")

      UseCsvParser.parse(line).getOrThrow().strain shouldBe null
    }

    test("Is read from wherever the header put the strain columns") {
      val use = UseArb.next()
      val line = (use.columns() + listOf("a note", "a tag") + strain.columns()).joinToString(",")

      UseCsvParser.parse(line, strainColumns = 8).getOrThrow().strain shouldBe parsedStrain
    }

    test("Trims the strain name and id") {
      val parsed = strainOf(line(" ${strain.id} ", "  ${strain.name}  "))!!

      parsed.id shouldBe strain.id
      parsed.name shouldBe strain.name
    }

    context("Potency") {
      fun potencies(thc: String, cbd: String) = strainOf(line(strain.id, strain.name, thc, cbd))!!.let {
        it.thcPercent to it.cbdPercent
      }

      test("Is dropped when it can't be read, without failing the line") {
        potencies("lots", "1") shouldBe (null to BigDecimal("1"))
      }

      test("Is read trimmed, and with a percent sign") {
        potencies(" 27.5", "1 %") shouldBe (BigDecimal("27.5") to BigDecimal("1"))
      }

      test("Keeps the bounds 0 and 100, and drops anything outside them") {
        potencies("100", "0") shouldBe (BigDecimal("100") to BigDecimal("0"))
        potencies("-5", "250") shouldBe (null to null)
      }

      test("Is read with an exponent or sign as long as it stays short") {
        potencies("2.7E1", "+1") shouldBe (BigDecimal("2.7E1") to BigDecimal("1"))
      }

      test("Is dropped when its exponent is so far out it can't be stored") {
        potencies("1E+999999999", "1E-999999999") shouldBe (null to null)
        potencies("0E-999999999", "1") shouldBe (null to BigDecimal("1"))
      }

      test("Keeps more than six decimals, so a stored potency survives a backup") {
        potencies("22.1234567", "1") shouldBe (BigDecimal("22.1234567") to BigDecimal("1"))
      }

      test("Reads as none when the columns are missing") {
        strainOf(line(strain.id, strain.name)) shouldBe CsvStrain(strain.id, strain.name)
      }
    }

    test("Drops a default cost that is negative or can't be read") {
      listOf("-1", "1E+3000000", "twelve").forEach { cost ->
        strainOf(line(strain.id, strain.name, "", "", cost, "false"))!!.costPerGram shouldBe null
      }
    }

    test("Reads the archived flag as true or 1, and anything else as active") {
      fun archivedFrom(value: String) = strainOf(line(strain.id, strain.name, "", "", "", value))!!.isArchived

      archivedFrom("true") shouldBe true
      archivedFrom("TRUE") shouldBe true
      archivedFrom("1") shouldBe true
      archivedFrom("false") shouldBe false
      archivedFrom("") shouldBe false
      archivedFrom("yes") shouldBe false
    }
  }
  context("strainColumnsIn") {
    val useLabels = listOf("date", "amount", "cost", "id", "description", "method")

    test("Finds the strain columns right after the use's own, as the app writes them") {
      UseCsvParser.strainColumnsIn((useLabels + Strain.CsvHeader).joinToString(",")) shouldBe 6
    }

    test("Finds them after columns a user added, and ignores columns added after them") {
      val header = useLabels + listOf("notes", "tags") + Strain.CsvHeader + listOf("rating")

      UseCsvParser.strainColumnsIn(header.joinToString(",")) shouldBe 8
    }

    test("Tolerates spaces around the labels") {
      UseCsvParser.strainColumnsIn((useLabels + Strain.CsvHeader.map { " $it " }).joinToString(",")) shouldBe 6
    }

    test("Finds none in a header from before strains, or one with columns a user added instead") {
      UseCsvParser.strainColumnsIn(useLabels.joinToString(",")) shouldBe null
      val userColumns = useLabels + listOf("notes", "tags", "a", "b", "c", "d")
      UseCsvParser.strainColumnsIn(userColumns.joinToString(",")) shouldBe null
    }

    test("Finds none when the strain labels are incomplete or out of order") {
      UseCsvParser.strainColumnsIn((useLabels + Strain.CsvHeader.dropLast(1)).joinToString(",")) shouldBe null
      UseCsvParser.strainColumnsIn((useLabels + Strain.CsvHeader.reversed()).joinToString(",")) shouldBe null
    }

    test("Finds none in a file without a header, whose first line is a use") {
      UseCsvParser.strainColumnsIn(UseArb.next().columns().joinToString(",")) shouldBe null
    }

    test("Finds none in a line that isn't CSV") {
      UseCsvParser.strainColumnsIn("\"unterminated") shouldBe null
    }
  }
})
