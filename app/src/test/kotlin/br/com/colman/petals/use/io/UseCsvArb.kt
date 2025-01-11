package br.com.colman.petals.use.io

import br.com.colman.petals.use.UseArb
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import java.time.format.DateTimeFormatter

val UseCsvArb = UseArb.map { it.columns().joinToString(",") }

class UseCsvArbitraryTest : FunSpec({

  test("Generated CSV strings have the correct number of fields") {
    checkAll(UseCsvArb) { csvString ->
      val fields = csvString.split(",")
      fields shouldHaveSize 5
    }
  }

  test("CSV fields are in the expected order and correctly represent the data") {
    checkAll(UseArb) { use ->
      val csvString = use.columns().joinToString(",")
      val fields = csvString.split(",")
      val expectedFields = use.columns()
      fields shouldBe expectedFields
    }
  }

  test("DateTime is correctly formatted in ISO_LOCAL_DATE_TIME") {
    val dateTimePattern = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    checkAll(UseCsvArb) { csvString ->
      val dateTimeField = csvString.split(",")[0]
      shouldNotThrowAny { dateTimePattern.parse(dateTimeField) }
    }
  }

  test("Amount and Cost are correctly formatted as plain strings") {
    checkAll(UseCsvArb) { csvString ->
      val fields = csvString.split(",")
      val amountField = fields[1]
      val costField = fields[2]
      amountField shouldBe amountField.toBigDecimal().toPlainString()
      costField shouldBe costField.toBigDecimal().toPlainString()
    }
  }
})
