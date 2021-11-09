package br.com.colman.petals.clock

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import org.joda.time.LocalDateTime
import org.joda.time.LocalDateTime.parse


class QuitTimer (private val context: Context) {

   private val Context.datastore: DataStore<Preferences> by preferencesDataStore("quit_timer")

   val quitDate = context.datastore.data.map { preferences ->
      val date = preferences[stringPreferencesKey("stopDate")]
      date?.let { parse(it) }
   }

   suspend fun setQuitDate(date: LocalDateTime) {
      context.datastore.edit { it[stringPreferencesKey("stopDate")] = date.toString() }
   }

}