package br.com.colman.petals.use

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class UseRepositoryTests : FunSpec({

  val target = UseRepository()
  val use = Use(1_00, 2_75)

  context("Use") {
    test("Converts grams") {
      use.amountGrams shouldBe BigDecimal("1.000")
    }
    test("Convert cost") {
      use.costPerGram shouldBe BigDecimal("2.750")
    }
  }

})