package br.com.colman.petals.playstore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import br.com.colman.petals.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class AdsSettingsRepository(
  private val datastore: DataStore<Preferences>
) {

   val isAdsFree: Flow<Boolean> = datastore.data.map { it[isAdFree] ?: false }

  fun setAdFree(value: Boolean): Unit = runBlocking {
    datastore.edit {
      it[isAdFree] = value
    }
  }

  companion object {
    val isAdFree = booleanPreferencesKey("is_adfree")
  }

}
