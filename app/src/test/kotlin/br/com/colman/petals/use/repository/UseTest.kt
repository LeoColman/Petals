package br.com.colman.petals.use.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.bigdecimal.shouldBeEqualIgnoringScale
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.time.LocalDateTime

class UseTest : FunSpec({
  context("Equals") {
    test("same instance should be equal") {
      val use = Use()
      use shouldBeEqual use
    }

    test("different instances with same fields (except id) should be equal") {
      val date = LocalDateTime.of(2025, 2, 11, 10, 0)
      val amount = BigDecimal("100.00")
      val cost = BigDecimal("2.50")
      val description = "Some Description"

      val use1 = Use(date = date, amountGrams = amount, costPerGram = cost, description = description, id = "111")
      val use2 = Use(date = date, amountGrams = amount, costPerGram = cost, description = description, id = "222")

      use1 shouldBeEqual use2
    }

    test("Different types should not be equal") {
      val use = Use()
      use shouldNotBeEqual Any()
    }

    test("Should not equal null") {
      val use = Use()
      (use == null) shouldBe false
    }

    test("different date should not be equal") {
      val date1 = LocalDateTime.of(2025, 2, 11, 10, 0)
      val date2 = LocalDateTime.of(2025, 2, 11, 10, 1)

      val use1 = Use(date = date1)
      val use2 = Use(date = date2)

      use1 shouldNotBeEqual use2
    }

    test("different amountGrams should not be equal") {
      val use1 = Use(amountGrams = BigDecimal("1.00"))
      val use2 = use1.copy(amountGrams = BigDecimal("2.00"))

      use1 shouldNotBeEqual use2
    }

    test("different costPerGram should not be equal") {
      val use1 = Use(costPerGram = BigDecimal("1.00"))
      val use2 = use1.copy(costPerGram = BigDecimal("2.00"))

      use1 shouldNotBeEqual use2
    }

    test("different description should not be equal") {
      val use1 = Use(description = "Description1")
      val use2 = use1.copy(description = "Description2")

      use1 shouldNotBeEqual use2
    }
  }

  context("Hashcode") {
    test("same instance => hash code should remain consistent") {
      val use = Use()
      use.hashCode() shouldBe use.hashCode()
    }

    test("two equal objects (except ID) => same hash code") {
      val date = LocalDateTime.of(2025, 2, 11, 10, 0)
      val amount = BigDecimal("100.00")
      val cost = BigDecimal("2.50")
      val description = "Some Description"

      val use1 = Use(date = date, amountGrams = amount, costPerGram = cost, description = description, id = "111")
      val use2 = Use(date = date, amountGrams = amount, costPerGram = cost, description = description, id = "222")

      use1 shouldBeEqual use2
      use1.hashCode() shouldBe use2.hashCode()
    }

    test("different date => different hash code") {
      val use1 = Use(date = LocalDateTime.of(2025, 2, 11, 10, 0))
      val use2 = Use(date = LocalDateTime.of(2025, 2, 11, 10, 1))

      use1 shouldNotBeEqual use2
      use1.hashCode() shouldNotBe use2.hashCode()
    }

    test("different amountGrams => different hash code") {
      val use1 = Use(amountGrams = BigDecimal("1.00"))
      val use2 = Use(amountGrams = BigDecimal("2.00"))

      use1 shouldNotBeEqual use2
      use1.hashCode() shouldNotBe use2.hashCode()
    }

    test("different costPerGram => different hash code") {
      val use1 = Use(costPerGram = BigDecimal("1.00"))
      val use2 = Use(costPerGram = BigDecimal("2.00"))

      use1 shouldNotBeEqual use2
      use1.hashCode() shouldNotBe use2.hashCode()
    }

    test("different description => different hash code") {
      val use1 = Use(description = "Description1")
      val use2 = Use(description = "Description2")

      use1 shouldNotBeEqual use2
      use1.hashCode() shouldNotBe use2.hashCode()
    }
  }

  test("columns() should return a list of stringified fields") {
    val date = LocalDateTime.of(2025, 2, 11, 11, 0, 30)
    val use = Use(
      date = date,
      amountGrams = BigDecimal("50.00"),
      costPerGram = BigDecimal("1.25"),
      id = "test-id",
      description = "Test description"
    )

    val columns = use.columns()

    columns.size shouldBe 5
    columns[0] shouldBe date.toString()
    columns[1] shouldBe "50.00"
    columns[2] shouldBe "1.25"
    columns[3] shouldBe "test-id"
    columns[4] shouldBe "Test description"
  }

  context("Total cost") {
    test("totalCost on empty list should be BigDecimal.ZERO") {
      val emptyList = emptyList<Use>()
      emptyList.totalCost shouldBe BigDecimal.ZERO
    }

    test("totalCost on single element should be costPerGram * amountGrams") {
      val useItem = Use(
        amountGrams = BigDecimal("5.00"),
        costPerGram = BigDecimal("2.00")
      )
      val list = listOf(useItem)

      list.totalCost shouldBeEqualIgnoringScale BigDecimal("10.00")
    }

    test("totalCost on multiple elements should be the sum of (costPerGram * amountGrams)") {
      val use1 = Use(amountGrams = BigDecimal("2.00"), costPerGram = BigDecimal("3.00"))
      val use2 = Use(amountGrams = BigDecimal("4.00"), costPerGram = BigDecimal("1.25"))
      val use3 = Use(amountGrams = BigDecimal("1.50"), costPerGram = BigDecimal("10.00"))

      val list = listOf(use1, use2, use3)

      list.totalCost shouldBeEqualIgnoringScale BigDecimal("26.00")
    }

    test("totalCost should handle zero and negative values") {
      val useZero = Use(amountGrams = BigDecimal.ZERO, costPerGram = BigDecimal("5.00"))
      val useNegative = Use(amountGrams = BigDecimal("-3.00"), costPerGram = BigDecimal("2.00"))
      val usePositive = Use(amountGrams = BigDecimal("2.00"), costPerGram = BigDecimal("3.00"))

      val list = listOf(useZero, useNegative, usePositive)

      list.totalCost shouldBeEqualIgnoringScale BigDecimal("0")
    }
  }

  context("Total grams") {
    test("totalGrams on empty list should be BigDecimal.ZERO") {
      val emptyList = emptyList<Use>()
      emptyList.totalGrams shouldBe BigDecimal.ZERO
    }

    test("totalGrams on single element should be the amountGrams of that element") {
      val useItem = Use(amountGrams = BigDecimal("5.00"))
      val list = listOf(useItem)

      list.totalGrams shouldBe BigDecimal("5.00")
    }

    test("totalGrams on multiple elements should be the sum of their amountGrams") {
      val use1 = Use(amountGrams = BigDecimal("2.50"))
      val use2 = Use(amountGrams = BigDecimal("7.50"))
      val use3 = Use(amountGrams = BigDecimal("10.00"))

      val list = listOf(use1, use2, use3)

      list.totalGrams shouldBe BigDecimal("20.00")
    }

    test("totalGrams should handle zero and negative values") {
      val useZero = Use(amountGrams = BigDecimal.ZERO)
      val useNegative = Use(amountGrams = BigDecimal("-3.00"))
      val usePositive = Use(amountGrams = BigDecimal("2.00"))

      val list = listOf(useZero, useNegative, usePositive)

      list.totalGrams shouldBe BigDecimal("-1.00")
    }
  }
})
