package br.com.colman.petals.components

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class StarRatingTest : FunSpec({

  context("A tap rates half a star short of the star in its first half, and the whole star in its second") {
    withData(
      nameFn = { (fraction, rating) -> "at $fraction of the way: $rating" },
      0.0f to 0.5,
      0.05f to 0.5,
      0.15f to 1.0,
      0.65f to 3.5,
      0.75f to 4.0,
      0.95f to 5.0,
      1.0f to 5.0,
    ) { (fraction, rating) ->
      ratingAt(fraction) shouldBe rating
    }
  }

  test("A tap just outside the stars still lands on the nearest one") {
    ratingAt(-0.1f) shouldBe 0.5
    ratingAt(1.1f) shouldBe 5.0
  }

  test("Tapping a new rating gives it") {
    ratingAfterTap(3.5, 4.0) shouldBe 3.5
    ratingAfterTap(3.5, null) shouldBe 3.5
  }

  test("Tapping the rating a use already has clears it") {
    ratingAfterTap(3.5, 3.5) shouldBe null
  }
})
