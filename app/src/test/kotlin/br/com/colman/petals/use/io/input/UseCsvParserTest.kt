package br.com.colman.petals.use.io.input

import br.com.colman.petals.use.UseArb
import br.com.colman.petals.use.io.UseCsvArb
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.shouldBe
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

    parsed.getOrThrow() shouldBe use
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

  test("Parses successfully even if extra fields are present") {
    val use = UseArb.next()
    val extraField = "extra"
    val useCsv = use.columns().plus(extraField).joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow() shouldBe use
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

    parsed.getOrThrow() shouldBe use
  }

  test("Parses successfully when amount and cost are zero") {
    val use = UseArb.next().copy(amountGrams = BigDecimal.ZERO, costPerGram = BigDecimal.ZERO)
    val useCsv = use.columns().joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow() shouldBe use
  }

  test("Parses successfully with very large amount and cost values") {
    val largeNumber = BigDecimal("9999999999999999999999999999.99")
    val use = UseArb.next().copy(amountGrams = largeNumber, costPerGram = largeNumber)
    val useCsv = use.columns().joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow() shouldBe use
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
})
