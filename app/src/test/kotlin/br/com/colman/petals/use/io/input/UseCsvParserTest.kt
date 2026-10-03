package br.com.colman.petals.use.io.input

import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.use.UseArb
import br.com.colman.petals.use.io.UseCsvArb
import br.com.colman.petals.use.repository.ConsumptionMethod
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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

    test("A line from before strains, without strain columns, has no strain and says so") {
      val use = UseArb.next()
      val row = UseCsvParser.parse(use.columns().joinToString(",")).getOrThrow()

      row.strain shouldBe null
      row.hasStrainColumns shouldBe false
    }

    test("A line with empty strain columns has no strain but does have the columns") {
      val use = UseArb.next()
      val row = UseCsvParser.parse((use.columns() + List(4) { "" }).joinToString(",")).getOrThrow()

      row.strain shouldBe null
      row.hasStrainColumns shouldBe true
    }

    test("Reads the strain the line names") {
      val use = UseArb.next()
      val row = UseCsvParser.parse((use.columns() + strain.columns()).joinToString(",")).getOrThrow()

      row.strain shouldBe strain
      row.use shouldBe use
    }

    test("Leaves the strain id off the use, for the importer to link") {
      val use = UseArb.next()
      val row = UseCsvParser.parse((use.columns() + strain.columns()).joinToString(",")).getOrThrow()

      row.use.strainId shouldBe null
    }

    test("A strain id without a name is not a strain") {
      val use = UseArb.next()
      val line = (use.columns() + listOf(strain.id, "", "27", "1")).joinToString(",")

      UseCsvParser.parse(line).getOrThrow().strain shouldBe null
    }

    test("A strain without an id gets one derived from its name") {
      fun idOf(name: String) =
        UseCsvParser.parse((UseArb.next().columns() + listOf("", name, "", "")).joinToString(",")).getOrThrow()
          .strain!!.id

      idOf(strain.name).shouldBeUUID()
      idOf(strain.name) shouldBe idOf(" ${strain.name.lowercase()} ")
      idOf(strain.name) shouldNotBe idOf("Bedrocan")
    }

    test("Trims the strain name") {
      val use = UseArb.next()
      val line = (use.columns() + listOf(strain.id, "  ${strain.name}  ", "", "")).joinToString(",")

      UseCsvParser.parse(line).getOrThrow().strain!!.name shouldBe strain.name
    }

    test("Drops an unreadable potency instead of failing the line") {
      val use = UseArb.next()
      val line = (use.columns() + listOf(strain.id, strain.name, "lots", "1")).joinToString(",")
      val parsed = UseCsvParser.parse(line).getOrThrow().strain!!

      parsed.thcPercent shouldBe null
      parsed.cbdPercent shouldBe BigDecimal("1")
    }

    test("Trims spaces around potencies, as a spreadsheet may add them") {
      val use = UseArb.next()
      val line = (use.columns() + listOf(strain.id, strain.name, " 27.5", "1 ")).joinToString(",")

      UseCsvParser.parse(line).getOrThrow().strain shouldBe strain
    }

    test("Drops a potency outside 0 to 100") {
      val use = UseArb.next()
      val line = (use.columns() + listOf(strain.id, strain.name, "-5", "250")).joinToString(",")
      val parsed = UseCsvParser.parse(line).getOrThrow().strain!!

      parsed.thcPercent shouldBe null
      parsed.cbdPercent shouldBe null
    }

    test("Keeps the bounds 0 and 100") {
      val use = UseArb.next()
      val line = (use.columns() + listOf(strain.id, strain.name, "100", "0")).joinToString(",")
      val parsed = UseCsvParser.parse(line).getOrThrow().strain!!

      parsed.thcPercent shouldBe BigDecimal("100")
      parsed.cbdPercent shouldBe BigDecimal("0")
    }

    test("Drops a potency with a huge exponent instead of failing later on it") {
      val use = UseArb.next()
      val line = (use.columns() + listOf(strain.id, strain.name, "1E+999999999", "1")).joinToString(",")

      UseCsvParser.parse(line).getOrThrow().strain!!.thcPercent shouldBe null
    }

    test("Missing potency columns read as no potency") {
      val use = UseArb.next()
      val line = (use.columns() + listOf(strain.id, strain.name)).joinToString(",")

      UseCsvParser.parse(line).getOrThrow().strain shouldBe Strain(strain.name, id = strain.id)
    }
  }
})
