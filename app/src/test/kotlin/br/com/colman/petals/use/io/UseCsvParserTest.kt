package br.com.colman.petals.use.io

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.take
import kotlin.random.Random

class UseCsvParserTest : FunSpec({
  test("Converts CSV to Use") {
    val use = useArb.next()
    val useCsv = use.columns().joinToString(",")
    val parsed = UseCsvParser.parse(useCsv)

    parsed.getOrThrow() shouldBe use
  }

  test("Returns failure if an invalid line is passed") {
    val useCsv = "invalid,cs,v"
    UseCsvParser.parse(useCsv).shouldBeFailure()
  }

  test("Returns failure if more than one line is passed") {
    val uses = useCsvArb.take(Random.nextInt(2, 1000)).toList().joinToString("\n")
    UseCsvParser.parse(uses).shouldBeFailure()
  }
})
