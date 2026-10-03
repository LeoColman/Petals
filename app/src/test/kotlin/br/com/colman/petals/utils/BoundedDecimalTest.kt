package br.com.colman.petals.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class BoundedDecimalTest : FunSpec({
  context("Reads what toBigDecimal reads, when its plain form stays short") {
    withData(
      "12.50" to BigDecimal("12.50"),
      "-3" to BigDecimal("-3"),
      ".5" to BigDecimal("0.5"),
      "+2" to BigDecimal("2"),
      "1e-3" to BigDecimal("0.001"),
      "2.7E1" to BigDecimal("27"),
      "1E+32" to BigDecimal("1E+32"),
      "1E-32" to BigDecimal("1E-32"),
    ) { (text, expected) ->
      text.toBoundedDecimalOrNull() shouldBe expected
    }
  }

  context("Refuses exponents so far out that toPlainString would run out of memory") {
    withData("1E+33", "1E-33", "1E+999999999", "1E-999999999", "0E-999999999") { text ->
      text.toBoundedDecimalOrNull() shouldBe null
    }
  }

  context("Refuses what isn't a number") {
    withData(nameFn = { "'$it'" }, "", "twelve", "1,5", " 1") { text ->
      text.toBoundedDecimalOrNull() shouldBe null
    }
  }
})
