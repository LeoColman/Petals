package br.com.colman.petals.use.repository.converter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

class LocalDateTimeConverterTest : FunSpec({

  val target = LocalDateTimeConverter()

  include(propertyConverterTests(target))

  test("Converts LocalDateTime to ISO DateTime String") {
    Arb.localDateTime().checkAll {
      target.convertToDatabaseValue(it) shouldBe it.format(ISO_LOCAL_DATE_TIME)
    }
  }

  test("Converts ISO DateTime String to LocalDateTime") {
    Arb.localDateTime().map { it to it.format(ISO_LOCAL_DATE_TIME) }.checkAll {
      val (dateTime, str) = it
      target.convertToEntityProperty(str) shouldBe dateTime
    }
  }

  test("Converts anything itself generate to something itself accepts") {
    Arb.localDateTime().checkAll {
      var string = target.convertToDatabaseValue(it)
      var dateTime = target.convertToEntityProperty(string)

      repeat(1000) {
        string = target.convertToDatabaseValue(dateTime)
        dateTime = target.convertToEntityProperty(string)
      }

      dateTime shouldBe it
      string shouldBe it.format(ISO_LOCAL_DATE_TIME)
    }
  }
})
