package br.com.colman.petals.use.repository.converter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bigDecimal
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll

class BigDecimalConverterTest : FunSpec({

  val target = BigDecimalConverter()

  include(propertyConverterTests(target))

  test("Converts BigDecimal to numeric, decimal strings") {
    Arb.bigDecimal().map { target.convertToDatabaseValue(it)!! }.checkAll {
      it shouldMatch "[-]?[0-9]+\\.[0-9]+"
    }
  }

  test("Converts numeric strings to big decimal") {
    Arb.bigDecimal().map { it to it.toPlainString() }.checkAll {
    val (bigDecimal, str) = it
      target.convertToEntityProperty(str) shouldBe bigDecimal
    }
  }
})
