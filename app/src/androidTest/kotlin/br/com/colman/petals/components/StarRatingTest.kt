package br.com.colman.petals.components

import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions.SetProgress
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runAndroidComposeUiTest
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.MainActivity
import br.com.colman.petals.R.string.rating_out_of_five
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalTestApi::class)
class StarRatingTest : FunSpec({

  fun AndroidComposeUiTest<MainActivity>.stars(initial: Double?): MutableState<Double?> {
    val rating = mutableStateOf(initial)
    activity!!.setContent { StarRating(rating.value, Modifier.testTag("Stars")) { rating.value = it } }
    return rating
  }

  fun AndroidComposeUiTest<MainActivity>.tapAt(fraction: Float) {
    onNodeWithTag("Stars").performTouchInput { click(percentOffset(fraction, 0.5f)) }
    waitForIdle()
  }

  test("Tapping the first half of the fourth star rates three and a half") {
    runAndroidComposeUiTest<MainActivity> {
      val rating = stars(null)
      tapAt(0.65f)
      rating.value shouldBe 3.5
    }
  }

  test("Tapping the second half of the fourth star rates four") {
    runAndroidComposeUiTest<MainActivity> {
      val rating = stars(2.0)
      tapAt(0.75f)
      rating.value shouldBe 4.0
    }
  }

  test("Tapping the rating it already has clears it") {
    runAndroidComposeUiTest<MainActivity> {
      val rating = stars(3.5)
      tapAt(0.65f)
      rating.value shouldBe null
    }
  }

  test("Screen readers set the rating to the nearest half star") {
    runAndroidComposeUiTest<MainActivity> {
      val rating = stars(null)
      onNodeWithTag("Stars").performSemanticsAction(SetProgress) { it(3.4f) }
      rating.value shouldBe 3.5
    }
  }

  test("Screen readers hear a rating that can't be changed") {
    runAndroidComposeUiTest<MainActivity> {
      activity!!.setContent { StarRating(3.5) }
      onNodeWithContentDescription(activity!!.getString(rating_out_of_five, "3.5")).assertIsDisplayed()
    }
  }
})
