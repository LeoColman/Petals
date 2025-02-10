package br.com.colman.petals.withdrawal.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class ChartConfigTest : FunSpec({

  test("Entries should contain all subclasses") {
    val classes = ChartConfig::class.sealedSubclasses

    ChartConfig.entries().map { it::class } shouldContainExactlyInAnyOrder classes
  }

  test("All subclasses but ThcConcentration should use the default x and y values") {
    ChartConfig.entries().forAll {
      if (it is ChartConfig.ThcConcentration) {
        it.maxX shouldBe 20.0
        it.maxY shouldBe 100.0
      } else {
        it.maxX shouldBe 25.0
        it.maxY shouldBe 10.0
      }
    }
  }
})
