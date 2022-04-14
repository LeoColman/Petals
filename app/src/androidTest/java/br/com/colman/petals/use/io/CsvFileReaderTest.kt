package br.com.colman.petals.use.io

import androidx.compose.material.Text
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject


class CsvFileReaderTest : KoinTest {
  @get:Rule
  val composeRule = createComposeRule()

  val target by inject<CsvFileReader>()

  @Test
  fun helloTargetExporterTest() {
    composeRule.setContent {
      Text("ABC")
      target.ReadCsvFile()
    }

  }
}