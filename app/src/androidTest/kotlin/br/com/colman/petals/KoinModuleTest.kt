@file:OptIn(ExperimentalTestApi::class)

package br.com.colman.petals

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runAndroidComposeUiTest
import br.com.colman.kotest.FunSpec
import org.koin.test.check.checkModules

class KoinModuleTest : FunSpec({
  test("Koin can resolve all modules") {
    runAndroidComposeUiTest<MainActivity> {
      koin.checkModules()
    }
  }
})
