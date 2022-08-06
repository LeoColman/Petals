package br.com.colman.petals.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class SettingsRepository(
  private val datastore: DataStore<Preferences>
) {

  val currencyIcon = datastore.data.map { it[CurrencyIcon] ?: "$" }

  fun setCurrencyIcon(value: String): Unit = runBlocking {
    datastore.edit { it[CurrencyIcon] = value }
  }

  companion object {
    val CurrencyIcon = stringPreferencesKey("currency_icon")
  }
}
