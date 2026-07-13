package br.com.colman.petals.use.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ConsumptionMethodTest : FunSpec({

  test("fromKey round-trips every value through its stable key") {
    ConsumptionMethod.entries.forEach { method ->
      ConsumptionMethod.fromKey(method.key) shouldBe method
    }
  }

  test("keys are the expected stable lowercase strings") {
    ConsumptionMethod.SMOKED.key shouldBe "smoked"
    ConsumptionMethod.VAPORIZED.key shouldBe "vaporized"
    ConsumptionMethod.EDIBLE.key shouldBe "edible"
    ConsumptionMethod.OTHER.key shouldBe "other"
  }

  test("fromKey returns null for an empty key") {
    ConsumptionMethod.fromKey("") shouldBe null
  }

  test("fromKey returns null for an unknown key") {
    ConsumptionMethod.fromKey("injected") shouldBe null
  }
})
