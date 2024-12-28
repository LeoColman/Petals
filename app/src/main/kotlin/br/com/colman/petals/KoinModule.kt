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
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import br.com.colman.petals.hittimer.HitTimerRepository
import br.com.colman.petals.settings.SettingsMigrations
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.io.UseIOModules
import br.com.colman.petals.use.pause.repository.PauseRepository
import br.com.colman.petals.use.repository.CensorshipRepository
import br.com.colman.petals.use.repository.UseRepository
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.dsl.module

private val Context.settingsDatastore by preferencesDataStore("settings")
private val Context.blockDataStore by preferencesDataStore("block")

val KoinModule = module {
  includes(AndroidModule, SqlDelightModule)
  includes(UseIOModules)

  single { UseRepository(get<Database>().useQueries) }
  single { PauseRepository(get<Database>().pauseQueries) }
  single { HitTimerRepository(get()) }
  single { SettingsRepository(get<Context>().settingsDatastore) }
  single { SettingsMigrations(get<Context>().settingsDatastore) }
  single { CensorshipRepository(get<Context>().blockDataStore) }
}

private val AndroidModule = module {
  single { get<Context>().resources }
  single { get<Context>().contentResolver }
}

private val SqlDelightModule = module {
  single<SqlDriver> {
    AndroidSqliteDriver(Database.Schema, get(), "Database", RequerySQLiteOpenHelperFactory())
  }
  single { Database(get()) }
}
