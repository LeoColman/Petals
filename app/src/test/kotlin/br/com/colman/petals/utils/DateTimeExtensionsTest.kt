package br.com.colman.petals.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class DateTimeExtensionsTest : FunSpec({

  context("LocalDateTime truncation") {

    test("should truncate to minute for current time") {
      val dateTime = LocalDateTime.now()
      val expectedDateTime = dateTime.truncatedTo(ChronoUnit.MINUTES)

      dateTime.truncatedToMinute() shouldBe expectedDateTime
    }

    test("should truncate to minute for a specific time") {
      val dateTime = LocalDateTime.of(1998, 2, 9, 20, 0, 45)
      val expectedDateTime = LocalDateTime.of(1998, 2, 9, 20, 0)

      dateTime.truncatedToMinute() shouldBe expectedDateTime
    }

    test("should handle truncation of an already truncated time") {
      val dateTime = LocalDateTime.of(1998, 2, 9, 20, 0)
      val expectedDateTime = LocalDateTime.of(1998, 2, 9, 20, 0)

      dateTime.truncatedToMinute() shouldBe expectedDateTime
    }
  }

  context("LocalTime truncation") {

    test("should truncate to minute for current time") {
      val time = LocalTime.now()
      val expectedTime = time.truncatedTo(ChronoUnit.MINUTES)

      time.truncatedToMinute() shouldBe expectedTime
    }

    test("should truncate to minute for a specific time") {
      val time = LocalTime.of(20, 0, 45)
      val expectedTime = LocalTime.of(20, 0)

      time.truncatedToMinute() shouldBe expectedTime
    }

    test("should handle truncation of an already truncated time") {
      val time = LocalTime.of(20, 30)
      val expectedTime = LocalTime.of(20, 30)

      time.truncatedToMinute() shouldBe expectedTime
    }

    test("should handle midnight correctly") {
      val time = LocalTime.MIDNIGHT
      val expectedTime = LocalTime.MIDNIGHT

      time.truncatedToMinute() shouldBe expectedTime
    }
  }
})
