package br.com.colman.petals.widget

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey

class WidgetRepository(
  private val datastore: DataStore<Preferences>,
) {

  companion object {
    val IconKey = intPreferencesKey("icon_key")
  }
}
