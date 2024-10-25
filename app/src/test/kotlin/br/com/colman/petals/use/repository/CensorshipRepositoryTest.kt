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

  test("Changes today's block censure") {
    target.isTodayCensored.first() shouldBe false
    target.setBlockCensure(BlockType.Today, true)
    target.isTodayCensored.first() shouldBe true
  }

  test("Persists specified block censure") {
    target.setBlockCensure(BlockType.Today, true)
    datastore.data.first()[BlockType.Today.preferencesKey] shouldBe true
  }
})
