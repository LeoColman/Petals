@file:OptIn(ExperimentalTestApi::class)

package br.com.colman.petals

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runAndroidComposeUiTest
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeUp
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.navigation.Page
import br.com.colman.petals.use.io.UseImporter
import br.com.colman.petals.use.repository.BlockRepository
import br.com.colman.petals.use.repository.BlockType
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FileDataPart
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale

val locales = listOf(
  "de" to "DE",
  "en" to "US",
  "fr" to "FR",
  "es" to "ES",
  "it" to "IT",
  "nl" to "NL",
  "no" to "NO",
  "pt" to "BR",
  "ru" to "RU",
  "tr" to "TR"
)

class ScreenshotTakerTest : FunSpec({

  var usesImported = false
  fun importUses(activity: MainActivity) {
    if (usesImported) return
    activity.importDemoUses()
    usesImported = true
  }

  test("ScreenShot 1") {
    runAndroidComposeUiTest<MainActivity> {
      importUses(activity!!)

      locales.forEach { (lang, country) ->
        activity?.setLocale(Locale(lang, country))

        val blockRepository by koin.inject<BlockRepository>()
        blockRepository.setBlockCensure(BlockType.Today, true)

        waitForIdle()
        takeScreenshot("1.png", lang, country)
      }
    }
  }

  test("ScreenShot 2") {
    runAndroidComposeUiTest<MainActivity> {
      importUses(activity!!)

      locales.forEach { (lang, country) ->
        activity?.setLocale(Locale(lang, country))

        val blockRepository by koin.inject<BlockRepository>()
        blockRepository.setBlockCensure(BlockType.Today, true)

        waitForIdle()

        onNodeWithTag("UsageMainColumn").performTouchInput {
          swipeUp(endY = bottom * 0.8f)
        }
        waitForIdle()
        onNodeWithTag("StatsBlockMainRow").performTouchInput {
          swipeLeft(endX = right * 0.75f)
        }

        waitForIdle()
        takeScreenshot("2.png", lang, country)
      }
    }
  }

  test("ScreenShot 3") {
    runAndroidComposeUiTest<MainActivity> {
      locales.forEach { (lang, country) ->
        activity?.setLocale(Locale(lang, country))

        onNodeWithTag(Page.HitTimer.name).performClick()
        waitForIdle()
        takeScreenshot("3.png", lang, country)
      }
    }
  }

  test("ScreenShot 4") {
    runAndroidComposeUiTest<MainActivity> {
      importUses(activity!!)

      locales.forEach { (lang, country) ->
        activity?.setLocale(Locale(lang, country))

        onNodeWithTag(Page.Symptoms.name).performClick()
        waitForIdle()

        takeScreenshot("4.png", lang, country)
      }
    }
  }

  test("ScreenShot 5") {
    runAndroidComposeUiTest<MainActivity> {
      importUses(activity!!)

      onNodeWithTag(Page.Stats.name).performClick()

      locales.forEach { (lang, country) ->
        activity?.setLocale(Locale(lang, country))

        waitForIdle()
        onNodeWithTag("Days 0").performClick()
        onNodeWithTag("Days 14").performClick()
        onNodeWithTag("Days 30").performClick()
        onNodeWithTag("Days 60").performClick()
        waitForIdle()

        takeScreenshot("5.png", lang, country)
      }
    }
  }

  test("ScreenShot 6") {
    runAndroidComposeUiTest<MainActivity> {
      importUses(activity!!)

      locales.forEach { (lang, country) ->
        activity?.setLocale(Locale(lang, country))

        waitForIdle()
        onNodeWithTag(Page.Stats.name).performClick()
        onNodeWithTag("Days 0").performClick()
        onNodeWithTag("Days 14").performClick()
        onNodeWithTag("Days 30").performClick()
        onNodeWithTag("Days 60").performClick()
        waitForIdle()
        onNodeWithTag("StatisticsMainColumn").performTouchInput {
          swipeUp(endY = bottom * 0.5f)
        }
        waitForIdle()

        takeScreenshot("6.png", lang, country)
      }
    }
  }

  test("ScreenShot 7") {
    runAndroidComposeUiTest<MainActivity> {
      locales.forEach { (lang, country) ->
        activity?.setLocale(Locale(lang, country))

        onNodeWithTag("InfoButton").performClick()
        onAllNodesWithTag("ExpandCollapseLine").onFirst().performClick()

        waitForIdle()

        takeScreenshot("7.png", lang, country)
      }
    }
  }

  test("ScreenShot 8") {
    runAndroidComposeUiTest<MainActivity> {
      locales.forEach { (lang, country) ->
        activity?.setLocale(Locale(lang, country))

        onNodeWithTag("InfoButton").performClick()
        onAllNodesWithTag("ExpandCollapseLine").onFirst().performClick()
        onNodeWithTag("InformationViewMainColumn").performTouchInput {
          swipeUp()
        }
        waitForIdle()
        onNodeWithTag("SelectCountry").performClick()
        onAllNodesWithTag("DropdownMenuItem").onFirst().performClick()
        waitForIdle()

        onNodeWithTag("InformationViewMainColumn").performTouchInput {
          swipeUp(endY = bottom * 0.7f)
        }

        waitForIdle()

        takeScreenshot("8.png", lang, country)
      }
    }
  }
})

private fun MainActivity.importDemoUses() {
  val useImporter by koin.inject<UseImporter>()

  val millisFromLastUseToToday = ChronoUnit.MILLIS.between(
    LocalDateTime.parse("2024-05-31T22:36:48.184"),
    LocalDateTime.now().withHour(22).withMinute(36).minusDays(1)
  )

  useImporter.import(assets.open("HeavyUse.csv").readAllBytes().decodeToString().split("\n")) {
    it.copy(date = it.date.plus(millisFromLastUseToToday, ChronoUnit.MILLIS))
  }.getOrThrow()
}

private fun MainActivity.setLocale(locale: Locale) {
  val resources = baseContext.resources
  Locale.setDefault(locale)
  val config = resources.configuration
  config.setLocale(locale)
  resources.updateConfiguration(config, resources.displayMetrics)
  runOnUiThread { recreate() }
}

private fun AndroidComposeUiTest<*>.takeScreenshot(file: String, lang: String, country: String) {
  val bitmap = onRoot().captureToImage().asAndroidBitmap()
  uploadScreenshot(bitmap, file, lang, country)
}

private fun uploadScreenshot(bitmap: Bitmap, fileName: String, lang: String, country: String) {
  val byteArrayOutputStream = ByteArrayOutputStream()
  bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
  val byteArray = byteArrayOutputStream.toByteArray()

  val tempFile = File.createTempFile(fileName, null).also { it.writeBytes(byteArray) }

  val computerIpAddress = "10.0.2.2"
  Fuel.upload("http://$computerIpAddress:8080/upload?country=$country&lang=$lang")
    .add(FileDataPart(tempFile, name = "file", filename = fileName))
    .response()
}
