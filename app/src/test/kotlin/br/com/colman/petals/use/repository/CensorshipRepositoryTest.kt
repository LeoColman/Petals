package br.com.colman.petals.use.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class CensorshipRepositoryTest : FunSpec({
  val datastore: DataStore<Preferences> = PreferenceDataStoreFactory.create { tempfile(suffix = ".preferences_pb") }
  val target = CensorshipRepository(datastore)

  test("Defaults block censor to false") {
    BlockType.entries.forEach { blockType ->
      val isCensored = when (blockType) {
        BlockType.Today -> target.isTodayCensored.first()
        BlockType.ThisWeek -> target.isThisWeekCensored.first()
        BlockType.ThisMonth -> target.isThisMonthCensored.first()
        BlockType.ThisYear -> target.isThisYearCensored.first()
        BlockType.AllTime -> target.isAllTimeCensored.first()
      }
      isCensored shouldBe false
    }
  }

  context("Changes block censure for all types") {
    BlockType.entries.forEach { blockType ->
      test("Sets and reflects censure for $blockType") {
        // Initially should be false
        val before = when (blockType) {
          BlockType.Today -> target.isTodayCensored.first()
          BlockType.ThisWeek -> target.isThisWeekCensored.first()
          BlockType.ThisMonth -> target.isThisMonthCensored.first()
          BlockType.ThisYear -> target.isThisYearCensored.first()
          BlockType.AllTime -> target.isAllTimeCensored.first()
        }
        before shouldBe false

        // Set to true and verify
        target.setBlockCensure(blockType, true)
        val afterTrue = when (blockType) {
          BlockType.Today -> target.isTodayCensored.first()
          BlockType.ThisWeek -> target.isThisWeekCensored.first()
          BlockType.ThisMonth -> target.isThisMonthCensored.first()
          BlockType.ThisYear -> target.isThisYearCensored.first()
          BlockType.AllTime -> target.isAllTimeCensored.first()
        }
        afterTrue shouldBe true

        // Set back to false and verify
        target.setBlockCensure(blockType, false)
        val afterFalse = when (blockType) {
          BlockType.Today -> target.isTodayCensored.first()
          BlockType.ThisWeek -> target.isThisWeekCensored.first()
          BlockType.ThisMonth -> target.isThisMonthCensored.first()
          BlockType.ThisYear -> target.isThisYearCensored.first()
          BlockType.AllTime -> target.isAllTimeCensored.first()
        }
        afterFalse shouldBe false
      }
    }
  }

  test("Persists specified block censure for all block types") {
    BlockType.entries.forEach { blockType ->
      target.setBlockCensure(blockType, true)
      datastore.data.first()[blockType.preferencesKey] shouldBe true
    }
  }
})
