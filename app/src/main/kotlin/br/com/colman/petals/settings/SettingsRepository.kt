package br.com.colman.petals.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.colman.petals.R.string.hours_12
import br.com.colman.petals.R.string.hours_24
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.time.format.DateTimeFormatter

/**
 * @see DateTimeFormatter
 */
private val DateFormats = listOf(
  "yyyy-MM-dd",
  "yyyy/MM/dd",
  "dd-MM-yyyy",
  "dd.MM.yyyy",
  "MM/dd/yyyy",
  "MM-dd-yyyy"
)

/**
 * @see DateTimeFormatter
 */
private val TimeFormats = listOf(
  "HH:mm",
  "KK:mm a",
  "HH:mm:ss",
  "KK:mm:ss a"
)

private val ClockFormats = listOf(hours_12, hours_24)

class SettingsRepository(
  private val datastore: DataStore<Preferences>
) {

  val currencyIcon = datastore.data.map { it[CurrencyIcon] ?: "$" }
  val dateFormatList = DateFormats
  val dateFormat = datastore.data.map { it[DateFormat] ?: dateFormatList.first() }
  val timeFormatList = TimeFormats
  val timeFormat = datastore.data.map { it[TimeFormat] ?: timeFormatList.first() }
  val clockFormatList = ClockFormats
  val is24HoursFormat = datastore.data.map { it[Is24HoursFormat] ?: false }
  val decimalPrecisionList = listOf(0, 1, 2, 3)
  val decimalPrecision = datastore.data.map { it[DecimalPrecision] ?: decimalPrecisionList[2] }
  val isDarkModeEnabled: Flow<Boolean> = datastore.data.map { it[IsDarkModeOn] ?: true }
  val isHitTimerMillisecondsEnabled = datastore.data.map { it[IsHitTimerMillisecondsEnabled] ?: false }
  val isHourOfDayLineInStatsEnabled = datastore.data.map { it[IsHourOfDayLineInStatsEnabled] ?: false }
  val isDayExtended = datastore.data.map { it[IsDayExtended] ?: false }
  val appLanguages = AppLanguage.entries.map { it.languageName }

  fun setCurrencyIcon(value: String): Unit = runBlocking {
    datastore.edit { it[CurrencyIcon] = value }
  }

  fun setDateFormat(value: String): Unit = runBlocking {
    datastore.edit { it[DateFormat] = value }
  }

  fun setTimeFormat(value: String): Unit = runBlocking {
    datastore.edit { it[TimeFormat] = value }
  }

  fun setIs24HoursFormat(value: Boolean): Unit = runBlocking {
    datastore.edit { it[Is24HoursFormat] = value }
  }

  fun setDecimalPrecision(value: Int): Unit = runBlocking {
    datastore.edit { it[DecimalPrecision] = value }
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

  fun setIsHitTimerMillisecondsEnabled(value: Boolean): Unit = runBlocking {
    datastore.edit { it[IsHitTimerMillisecondsEnabled] = value }
  }

  fun setDayExtended(value: Boolean): Unit = runBlocking {
    datastore.edit { it[IsDayExtended] = value }
  }

  fun setIsHourOfDayLineInStatsEnabled(value: Boolean): Unit = runBlocking {
    datastore.edit { it[IsHourOfDayLineInStatsEnabled] = value }
  }

  companion object {
    val CurrencyIcon = stringPreferencesKey("currency_icon")
    val DateFormat = stringPreferencesKey("date_format")
    val TimeFormat = stringPreferencesKey("time_format")
    val Is24HoursFormat = booleanPreferencesKey("is_24_hours_format")
    val Pin = stringPreferencesKey("pin")
    val DecimalPrecision = intPreferencesKey("decimal_precision")
    val IsDarkModeOn: Preferences.Key<Boolean> = booleanPreferencesKey("is_dark_mode_on")
    val IsHitTimerMillisecondsEnabled = booleanPreferencesKey("is_hit_timer_milliseconds_enabled")
    val IsDayExtended = booleanPreferencesKey("is_day_extended_enabled")
    val IsHourOfDayLineInStatsEnabled = booleanPreferencesKey("is_hour_of_day_line_in_stats_enabled")
  }
}
