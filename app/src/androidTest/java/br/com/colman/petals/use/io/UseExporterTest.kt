package br.com.colman.petals.use.io

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject


class UseExporterTest : KoinTest {

  @get:Rule
  val composeRule = createComposeRule()

  val target by inject<UseExporter>()

  @Test
  fun helloTargetExporterTest() {
    composeRule.setContent {
      val coroutine = rememberCoroutineScope()
      val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
      coroutine.launch {
        target.exportUses(launcher)
      }
    }
    println(target)
  }

}