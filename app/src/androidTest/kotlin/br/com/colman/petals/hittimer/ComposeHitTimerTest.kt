package br.com.colman.petals.hittimer

import androidx.activity.compose.setContent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runAndroidComposeUiTest
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.compose.ui.unit.dp
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.MainActivity
import br.com.colman.petals.koin
import br.com.colman.petals.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * The countdown format is a user setting, and these tests were written against the millisecond one
 * while [SettingsRepository.isHitTimerMillisecondsEnabled] defaults to off. That left them asserting
 * "10:000" against a screen reading "10", so every one of them failed on a device. Each test now
 * pins the format it needs rather than inheriting whatever the device happens to hold.
 */
@OptIn(ExperimentalTestApi::class)
class ComposeHitTimerTest : FunSpec({

  val settingsRepository = koin.get<SettingsRepository>()

  // Writing the preference is not enough: the store hands the new value to collectAsState on its own
  // schedule, so composing straight after the write can still read the previous format and the
  // assertion races it. Block until the flow reports the value we asked for.
  fun useMillisecondFormat(enabled: Boolean) {
    settingsRepository.setIsHitTimerMillisecondsEnabled(enabled)
    runBlocking { settingsRepository.isHitTimerMillisecondsEnabled.first { it == enabled } }
  }

  test("Start Timer Test") {
    runAndroidComposeUiTest<MainActivity> {
      useMillisecondFormat(true)
      activity!!.setContent {
        ComposeHitTimer()
      }

      onNodeWithText("10:000").assertExists()
      onNodeWithText("Start").performClick()
      waitUntilExactlyOneExists(hasText("09:0", true), TimeoutMillis)
      onNodeWithText("10:000").assertDoesNotExist()
    }
  }

  test("Reset Timer Test") {
    runAndroidComposeUiTest<MainActivity> {
      useMillisecondFormat(true)
      activity!!.setContent {
        ComposeHitTimer()
      }

      onNodeWithText("Start").performClick()
      waitUntilExactlyOneExists(hasText("09:0", true), TimeoutMillis)
      onNodeWithText("Reset").performClick()
      // Reset does not repaint the countdown on its own: millisElapsed only re-emits after its
      // delay(100), so asserting straight away races the flow.
      waitUntilExactlyOneExists(hasText("10:000"), TimeoutMillis)
    }
  }

  test("Timer Completes Test") {
    runAndroidComposeUiTest<MainActivity> {
      useMillisecondFormat(true)
      activity!!.setContent {
        ComposeHitTimer()
      }

      onNodeWithText("Start").performClick()
      waitUntilExactlyOneExists(hasText("00:000"), TimeoutMillis + 10_000)
      onNodeWithText("00:000").assertExists()
    }
  }

  test("UI Element Visibility Test") {
    runAndroidComposeUiTest<MainActivity> {
      useMillisecondFormat(true)
      activity!!.setContent {
        ComposeHitTimer()
      }

      onNodeWithText("10:000").assertExists()
      onNodeWithText("Start").assertExists()
      onNodeWithText("Reset").assertExists()
      onNodeWithText("Vibrate on timer end").assertExists()
    }
  }

  // Handing the toggle to the row costs the checkbox the sizing Material only applies while a
  // control handles its own changes, which collapses the space around the box.
  test("the vibrate row keeps a full touch target") {
    runAndroidComposeUiTest<MainActivity> {
      useMillisecondFormat(false)
      activity!!.setContent {
        ComposeHitTimer()
      }

      onNode(hasText("Vibrate on timer end") and isToggleable()).assertHeightIsAtLeast(48.dp)
    }
  }

  // The format users actually get by default, and the one nothing covered until now.
  test("countdown uses whole seconds when milliseconds are off") {
    runAndroidComposeUiTest<MainActivity> {
      useMillisecondFormat(false)
      activity!!.setContent {
        ComposeHitTimer()
      }

      onNodeWithText("10").assertExists()
      onNodeWithText("10:000").assertDoesNotExist()
    }
  }

  test("countdown drops to one decimal under a second") {
    runAndroidComposeUiTest<MainActivity> {
      useMillisecondFormat(false)
      activity!!.setContent {
        ComposeHitTimer()
      }

      onNodeWithText("Start").performClick()
      waitUntilExactlyOneExists(hasText("0.0"), TimeoutMillis + 10_000)
    }
  }
})

/**
 * The countdown ticks once a second, so any of these waits is generous on a developer machine. It is
 * the shared CI emulator that needs the headroom: at five seconds this spec failed roughly one run in
 * three once it ran alongside the rest of the suite. The budget still fails a genuinely stuck timer.
 */
private const val TimeoutMillis = 20_000L
