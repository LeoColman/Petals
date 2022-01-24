package br.com.colman.petals.use

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.objectbox.kotlin.boxFor
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

class UseRepositoryTest : FunSpec({

  context("BigDecimal converter") {
    val target = BigDecimalConverter()

    test("Converts to string") {
      target.convertToDatabaseValue(BigDecimal("19.255")) shouldBe "19.255"
    }

    test("Converts from string") {
      target.convertToEntityProperty("19.255") shouldBe BigDecimal("19.255")
    }
  }

  context("LocalDateTime converter") {
    val target = LocalDateTimeConverter()

    val ldt = LocalDateTime.now()
    val ldtString = ldt.format(ISO_LOCAL_DATE_TIME)

    test("Converts to string") {
      target.convertToDatabaseValue(ldt) shouldBe ldtString
    }

    test("Converts from string") {
      target.convertToEntityProperty(ldtString) shouldBe ldt
    }
  }

  context("Use Repository") {
    val box = MyObjectBox.builder().build().boxFor<Use>()
    beforeEach { box.removeAll() }

    val target = UseRepository(box)

    val use = Use(BigDecimal("1234.567"), BigDecimal("123.01"))

    test("Insert") {
      target.insert(use)
      box.all.single() shouldBe use
    }

    test("Get all") {
      box.put(use)
      target.all().first().single() shouldBe use
    }

    test("Last use date") {
      val useBefore = use.copy(date = use.date.minusYears(1))
      box.put(use)
      box.put(useBefore)

      target.getLastUseDate().first() shouldBe useBefore.date
    }
  }
})
