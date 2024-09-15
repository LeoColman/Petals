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

package br.com.colman.petals

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import br.com.colman.petals.hittimer.HitTimerRepository
import br.com.colman.petals.playstore.AdsSettingsRepository
import br.com.colman.petals.settings.SettingsMigrations
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.pause.repository.PauseRepository
import br.com.colman.petals.use.repository.BlockRepository
import br.com.colman.petals.use.repository.UseRepository
import org.koin.dsl.module

private val Context.settingsDatastore by preferencesDataStore("settings")
private val Context.blockDataStore by preferencesDataStore("block")
private val Context.adsSettings by preferencesDataStore("ads_settings")

val KoinModule = module {
  single { UseRepository(get<Database>().useQueries) }
  single { PauseRepository(get<Database>().pauseQueries) }
  single { HitTimerRepository(get()) }
  single { SettingsRepository(get<Context>().settingsDatastore) }
  single { AdsSettingsRepository(get<Context>().adsSettings) }
  single { SettingsMigrations(get<Context>().settingsDatastore) }
  single { BlockRepository(get<Context>().blockDataStore) }
}
