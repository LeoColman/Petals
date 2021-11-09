/*
 * Petals APP
 * Copyright (C) 2021 Leonardo Colman Lopes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package br.com.colman.petals.clock

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.joda.time.LocalDateTime
import org.joda.time.LocalDateTime.now
import org.joda.time.LocalDateTime.parse
import org.joda.time.Period

class QuitTimer(private val context: Context) {

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore("quit_timer")

    val quitDate = context.datastore.data.map { preferences ->
        val date = preferences[stringPreferencesKey("stopDate")]
        date?.let { parse(it) }
    }

    suspend fun setQuitDate(date: LocalDateTime) {
        context.datastore.edit { it[stringPreferencesKey("stopDate")] = date.toString() }
    }

    val periodFromStopDate = flow {
        while (true) {
            delay(1)
            val quitDate = quitDate.firstOrNull()
            if (quitDate == null) {
                emit(Period.ZERO)
            } else {
                emit(Period(quitDate, now()))
            }
        }
    }
}
