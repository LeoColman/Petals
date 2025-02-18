package br.com.colman.petals.statistics.graph.color

import androidx.compose.ui.graphics.Color
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ColorsTest : FunSpec({
  test("createColor should return correct color for given days") {
    createColor(0) shouldBe Color.Green
    createColor(7) shouldBe Color.Blue
    createColor(14) shouldBe Color.Yellow
    createColor(28) shouldBe Color.Red
    createColor(56) shouldBe Color(0xFF4DD0E1)
    createColor(84) shouldBe Color(0XFFFF8A65)
    createColor(100) shouldBe Color.Red // Default case
  }
})
