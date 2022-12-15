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
  val dateFormatList = listOf("yyyy-MM-dd", "yyyy/MM/dd", "dd-MM-yyyy", "dd.MM.yyyy",
                               "MM/dd/yyyy", "MM-dd-yyyy")
  val dateFormat = datastore.data.map { it[DateFormat] ?: dateFormatList.first() }
  val timeFormatList = listOf("HH-mm", "HH:mm", "mm-HH", "mm:HH")
  val timeFormat = datastore.data.map { it[TimeFormat] ?: timeFormatList.first() }

  fun setCurrencyIcon(value: String): Unit = runBlocking {
    datastore.edit { it[CurrencyIcon] = value }
  }

  fun setDateFormat(value: String): Unit = runBlocking {
    datastore.edit { it[DateFormat] = value }
  }

  fun setTimeFormat(value: String): Unit = runBlocking {
    datastore.edit { it[TimeFormat] = value }
  }

  companion object {
    val CurrencyIcon = stringPreferencesKey("currency_icon")
    val DateFormat = stringPreferencesKey("date_format")
    val TimeFormat = stringPreferencesKey("time_format")
  }
}
