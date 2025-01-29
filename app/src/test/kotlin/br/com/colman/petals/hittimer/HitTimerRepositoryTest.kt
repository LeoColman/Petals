package br.com.colman.petals.hittimer

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class HitTimerRepositoryTest : FunSpec({

  val dataStore = PreferenceDataStoreFactory.create {
    tempfile(suffix = ".preferences_pb")
  }

  val target = HitTimerRepository(dataStore)

  test("Initial value of shouldVibrate should be false") {
    target.shouldVibrate.first() shouldBe false
  }

  test("setShouldVibrate(true) should update value to true") {
    target.setShouldVibrate(true)
    target.shouldVibrate.first() shouldBe true
  }

  test("setShouldVibrate(false) after setting true should update to false") {
    target.setShouldVibrate(true)
    target.shouldVibrate.first() shouldBe true

    target.setShouldVibrate(false)
    target.shouldVibrate.first() shouldBe false
  }

  isolationMode = IsolationMode.InstancePerTest
})
