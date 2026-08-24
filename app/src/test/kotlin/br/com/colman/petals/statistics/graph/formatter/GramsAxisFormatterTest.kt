package br.com.colman.petals.statistics.graph.formatter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GramsAxisFormatterTest : FunSpec({

  test("A sub-gram axis keeps two decimals, which is the whole of its range") {
    val format = gramsAxisFormatter(0.32f)

    format(0f) shouldBe "0.00"
    format(0.06f) shouldBe "0.06"
    format(0.32f) shouldBe "0.32"
  }

  test("A single digit axis keeps one decimal") {
    val format = gramsAxisFormatter(3.4f)

    format(0f) shouldBe "0.0"
    format(1.25f) shouldBe "1.3"
    format(3.4f) shouldBe "3.4"
  }

  test("A large axis drops decimals entirely") {
    val format = gramsAxisFormatter(64f)

    format(0f) shouldBe "0"
    format(16f) shouldBe "16"
    format(64.4f) shouldBe "64"
  }

  test("An empty chart still formats rather than dividing by its own range") {
    gramsAxisFormatter(0f)(0f) shouldBe "0.00"
  }
})
