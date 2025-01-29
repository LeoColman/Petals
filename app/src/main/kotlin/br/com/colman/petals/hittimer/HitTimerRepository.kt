package br.com.colman.petals.hittimer

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class HitTimerRepository(
  private val sharedPreferences: DataStore<Preferences>
) {

  val shouldVibrate = sharedPreferences.data.map { it[ShouldVibrate] ?: false }

  fun setShouldVibrate(value: Boolean) {
    runBlocking {
      sharedPreferences.edit { it[ShouldVibrate] = value }
    }
  }

  private companion object {
    val ShouldVibrate = booleanPreferencesKey("should_vibrate")
  }
}
