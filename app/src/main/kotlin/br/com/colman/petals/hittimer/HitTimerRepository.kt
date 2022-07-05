package br.com.colman.petals.hittimer

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Flow


class HitTimerRepository(private val context: Context) {

  private val Context.sharedPreferences by preferencesDataStore("hittimer_preferences")

  val shouldVibrate = context.sharedPreferences.data.map { it[ShouldVibrate] ?: false }

  fun setShouldVibrate(value: Boolean) {
    runBlocking {
      context.sharedPreferences.edit { it[ShouldVibrate] = value }
    }
  }

  private companion object {
    val ShouldVibrate = booleanPreferencesKey("should_vibrate")
  }
}