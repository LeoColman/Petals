package br.com.colman.petals.components

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runAndroidComposeUiTest
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.MainActivity
import br.com.colman.petals.R.string.date
import compose.icons.TablerIcons
import compose.icons.tablericons.Calendar
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalTestApi::class)
class ClickableTextFieldTest : FunSpec({

  // The field at the top of a form too tall for its 300dp, like the date in the add-use dialog with the keyboard open.
  fun AndroidComposeUiTest<MainActivity>.fieldInScrollingForm(): () -> Int {
    var clicks = 0
    activity!!.setContent {
      Column(Modifier.height(300.dp).verticalScroll(rememberScrollState())) {
        ClickableTextField(date, TablerIcons.Calendar, "2026-10-23", Modifier.testTag("Field")) { clicks++ }
        Spacer(Modifier.height(1000.dp))
      }
    }
    return { clicks }
  }

  test("Tapping the field clicks it once") {
    runAndroidComposeUiTest<MainActivity> {
      val clicks = fieldInScrollingForm()
      onNodeWithTag("Field").performClick()
      waitForIdle()
      clicks() shouldBe 1
    }
  }

  test("A scroll that starts on the field doesn't click it") {
    runAndroidComposeUiTest<MainActivity> {
      val clicks = fieldInScrollingForm()
      onNodeWithTag("Field").performTouchInput { swipeUp() }
      waitForIdle()
      clicks() shouldBe 0
    }
  }
})
