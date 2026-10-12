package br.com.colman.petals.use.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class RatingTest : FunSpec({

  context("ofOrNull keeps a rating in halves, rounding to the nearest half star") {
    withData(
      nameFn = { (stars, rating) -> "$stars stars: $rating" },
      0.5 to 0.5,
      3.0 to 3.0,
      3.5 to 3.5,
      5.0 to 5.0,
      3.7 to 3.5,
      3.8 to 4.0,
      4.9 to 5.0,
    ) { (stars, rating) ->
      Rating.ofOrNull(stars) shouldBe rating
    }
  }

  context("ofOrNull has no rating below half a star or above five") {
    withData(0.0, 0.2, -1.0, 5.3, 6.0, 10.0) { stars ->
      Rating.ofOrNull(stars) shouldBe null
    }
  }

  test("format writes whole stars without decimals and halves with them") {
    Rating.format(4.0) shouldBe "4"
    Rating.format(3.5) shouldBe "3.5"
    Rating.format(0.5) shouldBe "0.5"
  }
})
