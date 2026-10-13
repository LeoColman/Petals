package br.com.colman.petals.components

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.Locale

class StarRatingTest : FunSpec({

  test("A tap in a star's left half rates half a star short of it") {
    ratingAt(star = 4, x = 10f, width = 100, isRtl = false) shouldBe 3.5
    ratingAt(star = 1, x = 0f, width = 100, isRtl = false) shouldBe 0.5
  }

  test("A tap in a star's right half rates the whole star") {
    ratingAt(star = 4, x = 60f, width = 100, isRtl = false) shouldBe 4.0
    ratingAt(star = 5, x = 99f, width = 100, isRtl = false) shouldBe 5.0
  }

  test("In a right-to-left language a star's right half comes first") {
    ratingAt(star = 4, x = 60f, width = 100, isRtl = true) shouldBe 3.5
    ratingAt(star = 4, x = 10f, width = 100, isRtl = true) shouldBe 4.0
  }

  test("Tapping a new rating gives it") {
    ratingAfterTap(3.5, 4.0) shouldBe 3.5
    ratingAfterTap(3.5, null) shouldBe 3.5
  }

  test("Tapping the rating a use already has clears it") {
    ratingAfterTap(3.5, 3.5) shouldBe null
  }

  test("A rating reads out without a trailing zero, to one decimal at most") {
    ratingNumber(4.0, Locale.ENGLISH) shouldBe "4"
    ratingNumber(3.5, Locale.ENGLISH) shouldBe "3.5"
    ratingNumber(4.25, Locale.ENGLISH) shouldBe "4.3"
  }

  test("A rating reads out with the language's decimal separator") {
    ratingNumber(3.5, Locale.GERMAN) shouldBe "3,5"
  }
})
