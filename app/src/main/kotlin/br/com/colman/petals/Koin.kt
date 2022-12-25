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
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.io.IoModules
import br.com.colman.petals.use.pause.repository.PauseRepository
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.discomfort.repository.DiscomfortRepository
import br.com.colman.petals.withdrawal.discomfort.view.DiscomfortView
import br.com.colman.petals.withdrawal.thc.repository.ThcConcentrationRepository
import br.com.colman.petals.withdrawal.thc.view.ThcConcentrationView
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.module

lateinit var koin: Koin
  private set

fun Context.initializeKoin() {
  if (::koin.isInitialized) return
  koin = startKoin {
    androidContext(this@initializeKoin)
    modules(KoinModule)
    modules(AndroidModule)
    modules(SqlDelightModule)
    modules(IoModules)
  }.koin
}

private val Context.settingsDatastore by preferencesDataStore("settings")
val KoinModule = module {
  single { UseRepository(get<Database>().useQueries) }
  single { PauseRepository(get<Database>().pauseQueries) }
  single { HitTimerRepository(get()) }
  single { SettingsRepository(get<Context>().settingsDatastore) }

  single { ThcConcentrationRepository(get()) }
  single { ThcConcentrationView(get(), get()) }

  single { DiscomfortRepository(get()) }
  single { DiscomfortView(get(), get()) }
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