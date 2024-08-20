package br.com.colman.petals.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import br.com.colman.petals.settings.SettingsRepository.Companion.ClockFormat
import br.com.colman.petals.settings.SettingsRepository.Companion.ExtendedDayEnabled
import br.com.colman.petals.settings.SettingsRepository.Companion.HitTimerMillisecondsEnabled
import br.com.colman.petals.settings.SettingsRepository.Companion.Is24HoursFormat
import br.com.colman.petals.settings.SettingsRepository.Companion.IsDayExtended
import br.com.colman.petals.settings.SettingsRepository.Companion.IsHitTimerMillisecondsEnabled
import br.com.colman.petals.settings.SettingsRepository.Companion.MillisecondsEnabled
import kotlinx.coroutines.runBlocking

class SettingsMigrations(private val datastore: DataStore<Preferences>) {

  fun migrateOldKeysValues(): Unit = runBlocking {
    datastore.edit { prefs ->
      val hitTimerMillisecondsEnabled = prefs[HitTimerMillisecondsEnabled]
      val extendedDay = prefs[ExtendedDayEnabled]
      val clockFormat = prefs[ClockFormat]

      if (hitTimerMillisecondsEnabled != null) {
        if (hitTimerMillisecondsEnabled == "enabled") {
          prefs[IsHitTimerMillisecondsEnabled] = true
        } else {
          prefs[IsHitTimerMillisecondsEnabled] = false
        }
      }

      if (extendedDay != null) {
        if (extendedDay == "enabled") {
          prefs[IsDayExtended] = true
        } else {
          prefs[IsDayExtended] = false
        }
      }

      if (clockFormat != null) {
        prefs[Is24HoursFormat] = extendedDay == "24 hours"
      }
    }
  }

  fun removeOldKeysValues(): Unit = runBlocking {
    datastore.edit { prefs ->
      prefs.remove(HitTimerMillisecondsEnabled)
      prefs.remove(ExtendedDayEnabled)
      prefs.remove(MillisecondsEnabled)
    }
  }
}
