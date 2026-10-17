package br.com.colman.petals.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.util.Locale

class LocaleTest : FunSpec({

  test("A decimal is written to the given places, rounding half up") {
    decimalText(BigDecimal("6.875"), 2, Locale.ENGLISH) shouldBe "6.88"
    decimalText(BigDecimal("0.5"), 2, Locale.ENGLISH) shouldBe "0.50"
    decimalText(BigDecimal("2.5"), 0, Locale.ENGLISH) shouldBe "3"
  }

  test("A decimal is written as the language writes numbers") {
    decimalText(BigDecimal("1234.5"), 2, Locale.ENGLISH) shouldBe "1,234.50"
    decimalText(BigDecimal("1234.5"), 2, Locale.GERMAN) shouldBe "1.234,50"
  }
})
