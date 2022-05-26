package br.com.colman.petals.use.io

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject


class UseCsvFileImporterTest : KoinTest {
  @get:Rule
  val composeRule = createComposeRule()

  val target by inject<UseCsvFileImporter>()

  @Test
  fun helloTargetExporterTest() {
  }
}