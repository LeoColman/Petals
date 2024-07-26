package br.com.colman.petals.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
  val millisecondsEnabled = datastore.data.map { it[MillisecondsEnabled] ?: false }
  val hitTimerMillisecondsEnabled = datastore.data.map { it[HitTimerMillisecondsEnabled] ?: true }
  val decimalPrecisionList = listOf(0, 1, 2, 3)
  val decimalPrecision = datastore.data.map { it[DecimalPrecision] ?: decimalPrecisionList[2] }
  val extendedDay = datastore.data.map { it[ExtendedDayEnabled] ?: false }
  val isDarkModeEnabled: Flow<Boolean> = datastore.data.map { it[IsDarkModeOn] ?: true }

  fun setCurrencyIcon(value: String): Unit = runBlocking {
    datastore.edit { it[CurrencyIcon] = value }
  }

  fun setDateFormat(value: String): Unit = runBlocking {
    datastore.edit { it[DateFormat] = value }
  }

  fun setTimeFormat(value: String): Unit = runBlocking {
    datastore.edit { it[TimeFormat] = value }
  }

  fun setMillisecondsEnabled(value: Boolean): Unit = runBlocking {
    datastore.edit { it[MillisecondsEnabled] = value }
  }

  fun setHitTimerMillisecondsEnabled(value: Boolean): Unit = runBlocking {
    datastore.edit { it[HitTimerMillisecondsEnabled] = value }
  }

  fun setDecimalPrecision(value: Int): Unit = runBlocking {
    datastore.edit { it[DecimalPrecision] = value }
  }

  fun setExtendedDay(value: Boolean): Unit = runBlocking {
    datastore.edit { it[ExtendedDayEnabled] = value }
  }

  fun setDarkMode(value: Boolean): Unit = runBlocking {
    datastore.edit { it[IsDarkModeOn] = value }
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
    val MillisecondsEnabled = booleanPreferencesKey("milliseconds_enabled")
    val HitTimerMillisecondsEnabled = booleanPreferencesKey("hit_timer_milliseconds_enabled")
    val DecimalPrecision = intPreferencesKey("decimal_precision")
    val ExtendedDayEnabled = booleanPreferencesKey("is_day_extended")
    val IsDarkModeOn = booleanPreferencesKey("is_dark_mode_on")
  }
}
