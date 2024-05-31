package br.com.colman.petals.hittimer

import androidx.activity.compose.setContent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runAndroidComposeUiTest
import androidx.compose.ui.test.waitUntilExactlyOneExists
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.MainActivity


@OptIn(ExperimentalTestApi::class)
class ComposeHitTimerTest : FunSpec({

  test("Start Timer Test") {
    runAndroidComposeUiTest<MainActivity> {
      activity!!.setContent {
        ComposeHitTimer()
      }

      onNodeWithText("10:000").assertExists()
      onNodeWithText("Start").performClick()
      waitUntilExactlyOneExists(hasText("09:0", true), 5000)
      onNodeWithText("10:000").assertDoesNotExist()

    }
  }

  test("Reset Timer Test") {
    runAndroidComposeUiTest<MainActivity> {
      activity!!.setContent {
        ComposeHitTimer()
      }

      onNodeWithText("Start").performClick()
      waitUntilExactlyOneExists(hasText("09:0", true), 5000)
      onNodeWithText("Reset").performClick()
      onNodeWithText("10:000").assertExists()
    }
  }

  test("Timer Completes Test") {
    runAndroidComposeUiTest<MainActivity> {
      activity!!.setContent {
        ComposeHitTimer()
      }

      onNodeWithText("Start").performClick()
      waitUntilExactlyOneExists(hasText("00:000"), 11000)
      onNodeWithText("00:000").assertExists()
    }
  }

  test("UI Element Visibility Test") {
    runAndroidComposeUiTest<MainActivity> {
      activity!!.setContent {
        ComposeHitTimer()
      }

      onNodeWithText("10:000").assertExists()
      onNodeWithText("Start").assertExists()
      onNodeWithText("Reset").assertExists()
      onNodeWithText("Vibrate on timer end").assertExists()
    }
  }
})
