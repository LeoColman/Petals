package br.com.colman.petals.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class SettingsRepository(
  private val datastore: DataStore<Preferences>
) {

  val currencyIcon = datastore.data.map { it[CurrencyIcon] ?: "$" }
  val dateFormatList = listOf(
    "yyyy-MM-dd",
    "yyyy/MM/dd",
    "dd-MM-yyyy",
    "dd.MM.yyyy",
    "MM/dd/yyyy",
    "MM-dd-yyyy"
  )
  val dateFormat = datastore.data.map { it[DateFormat] ?: dateFormatList.first() }
  val timeFormatList = listOf("HH:mm", "KK:mm a", "HH:mm:ss", "KK:mm:ss a")
  val timeFormat = datastore.data.map { it[TimeFormat] ?: timeFormatList.first() }
  val millisecondsEnabledList = listOf("enabled", "disabled")
  val millisecondsEnabled = datastore.data.map { it[MillisecondsEnabled] ?: millisecondsEnabledList.first() }
  val hitTimerMillisecondsEnabledList = listOf("enabled", "disabled")
  val hitTimerMillisecondsEnabled = datastore.data.map {
    it[HitTimerMillisecondsEnabled] ?: hitTimerMillisecondsEnabledList.first()
  }

  fun setCurrencyIcon(value: String): Unit = runBlocking {
    datastore.edit { it[CurrencyIcon] = value }
  }

  fun setDateFormat(value: String): Unit = runBlocking {
    datastore.edit { it[DateFormat] = value }
  }

  fun setTimeFormat(value: String): Unit = runBlocking {
    datastore.edit { it[TimeFormat] = value }
  }

  fun setMillisecondsEnabled(value: String): Unit = runBlocking {
    datastore.edit { it[MillisecondsEnabled] = value }
  }

  fun setHitTimerMillisecondsEnabled(value: String): Unit = runBlocking {
    datastore.edit { it[HitTimerMillisecondsEnabled] = value }
  }

  val pin: Flow<String?>
    get() = datastore.data.map { it[Pin] }

  fun setPin(pin: String?): Unit = runBlocking {
    datastore.edit {
      if (pin == null) it.remove(Pin) else it[Pin] = pin
    }
  }

  companion object {
    val CurrencyIcon = stringPreferencesKey("currency_icon")
    val DateFormat = stringPreferencesKey("date_format")
    val TimeFormat = stringPreferencesKey("time_format")
    val Pin = stringPreferencesKey("pin")
    val MillisecondsEnabled = stringPreferencesKey("milliseconds_enabled")
    val HitTimerMillisecondsEnabled = stringPreferencesKey("hit_timer_milliseconds_enabled")
  }
}
