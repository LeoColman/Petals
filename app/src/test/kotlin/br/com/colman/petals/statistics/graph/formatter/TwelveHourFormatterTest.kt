package br.com.colman.petals.statistics.graph.formatter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TwelveHourFormatterTest : FunSpec({
  val formatter = TwelveHourFormatter

  test("12.0 should return '12'") {
    formatter(12.0f) shouldBe "12"
  }

  test("0.0 should return '0'") {
    formatter(0.0f) shouldBe "0"
  }

  test("13.0 should return '1'") {
    formatter(13.0f) shouldBe "1"
  }

  test("24.0 should return '0'") {
    formatter(24.0f) shouldBe "0"
  }

  test("12.5 rounds to 13 and returns '1'") {
    formatter(12.5f) shouldBe "1"
  }

  test("11.5 rounds to 12 and returns '12'") {
    formatter(11.5f) shouldBe "12"
  }

  test("1.0 should return '1'") {
    formatter(1.0f) shouldBe "1"
  }

  test("23.0 should return '11'") {
    formatter(23.0f) shouldBe "11"
  }

  test("12.9 rounds to 13 and returns '1'") {
    formatter(12.9f) shouldBe "1"
  }

  test("11.4 rounds to 11 and returns '11'") {
    formatter(11.4f) shouldBe "11"
  }

  test("-1.0 should return '-1'") {
    formatter(-1.0f) shouldBe "-1"
  }

  test("-13.0 should return '-1'") {
    formatter(-13.0f) shouldBe "-1"
  }
})
