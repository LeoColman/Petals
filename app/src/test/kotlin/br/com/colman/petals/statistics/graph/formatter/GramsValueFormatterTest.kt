package br.com.colman.petals.statistics.graph.formatter

import com.github.mikephil.charting.data.Entry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GramsValueFormatterTest : FunSpec({
  val formatter = GramsValueFormatter

  test("formats positive value with two decimal places") {
    val entry = Entry(0f, 123.456f)
    formatter.getFormattedValue(999f, entry, 0, null) shouldBe "123.46g"
  }

  test("formats exact two decimal places correctly") {
    val entry = Entry(0f, 7.89f)
    formatter.getFormattedValue(0f, entry, 0, null) shouldBe "7.89g"
  }

  test("rounds up correctly") {
    val entry = Entry(0f, 2.999f)
    formatter.getFormattedValue(0f, entry, 0, null) shouldBe "3.00g"
  }

  test("formats zero value correctly") {
    val entry = Entry(0f, 0f)
    formatter.getFormattedValue(0f, entry, 0, null) shouldBe "0.00g"
  }

  test("formats negative value correctly") {
    val entry = Entry(0f, -3.5f)
    formatter.getFormattedValue(0f, entry, 0, null) shouldBe "-3.50g"
  }

  test("handles null entry by showing '0.00g'") {
    formatter.getFormattedValue(0f, null, 0, null) shouldBe "0.00g"
  }

  test("ignores value parameter and uses entry.y") {
    val entry = Entry(0f, 5f)
    formatter.getFormattedValue(10f, entry, 0, null) shouldBe "5.00g"
  }

  test("formats large numbers correctly") {
    val entry = Entry(0f, 999999.999f)
    formatter.getFormattedValue(0f, entry, 0, null) shouldBe "1000000.00g"
  }
})
