package br.com.colman.petals.components

import androidx.activity.compose.setContent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runAndroidComposeUiTest
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.MainActivity
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalTestApi::class)
class DateTimePickerTest : FunSpec({

  test("Open and select date in date dialog") {
    runAndroidComposeUiTest<MainActivity> {
      var selectedDate: LocalDate? = null

      activity!!.setContent {
        val dialogState = dateDialogState { date ->
          selectedDate = date
        }
        dialogState.show()
      }

      onNodeWithText("Select Date").assertExists().assertIsDisplayed()

      onNodeWithText("14").performClick()
      onNodeWithText("OK").performClick()

      selectedDate?.dayOfMonth shouldBe 14
    }
  }

  test("Open and cancel select date in date dialog") {
    runAndroidComposeUiTest<MainActivity> {
      var selectedDate: LocalDate? = null

      activity!!.setContent {
        val dialogState = dateDialogState { date ->
          selectedDate = date
        }
        dialogState.show()
      }

      onNodeWithText("Select Date").assertExists().assertIsDisplayed()

      onNodeWithText("14").performClick()
      onNodeWithText("CANCEL").performClick()

      selectedDate shouldBe null
    }
  }

  test("Open and select time in time dialog") {
    runAndroidComposeUiTest<MainActivity> {
      var selectedTime: LocalTime? = null

      activity!!.setContent {
        val dialogState = timeDialogState { time ->
          selectedTime = time
        }
        dialogState.show()
      }

      onNodeWithText("Select Time").assertExists().assertIsDisplayed()

      onNodeWithText("OK").performClick()

      selectedTime shouldNotBe null
    }
  }

  test("Open and cancel select time in time dialog") {
    runAndroidComposeUiTest<MainActivity> {
      var selectedTime: LocalTime? = null

      activity!!.setContent {
        val dialogState = timeDialogState { time ->
          selectedTime = time
        }
        dialogState.show()
      }

      onNodeWithText("Select Time").assertExists().assertIsDisplayed()

      onNodeWithText("CANCEL").performClick()

      selectedTime shouldBe null
    }
  }
})
