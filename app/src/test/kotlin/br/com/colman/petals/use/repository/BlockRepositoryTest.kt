package br.com.colman.petals.use.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class BlockRepositoryTest : FunSpec({
  val datastore: DataStore<Preferences> = PreferenceDataStoreFactory.create { tempfile(suffix = ".preferences_pb") }
  val target = BlockRepository(datastore)

  test("Defaults block censor true") {
    BlockType.entries.forEach { blockType ->
      val defaultBlockCensorValue = datastore.data.first()[blockType.preferencesKey]
      defaultBlockCensorValue shouldBe true
    }
  }

  test("Changes today's block censure") {
    target.setBlockCensure(BlockType.Today, false)
    target.isTodayCensored.first() shouldBe false
  }

  test("Persists specified block censure") {
    target.setBlockCensure(BlockType.Today, false)
    datastore.data.first()[BlockType.Today.preferencesKey] shouldBe false
  }
})
