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

import android.app.Application
import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import br.com.colman.petals.BuildConfig.DEBUG
import br.com.colman.petals.use.io.IoModules
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

lateinit var koin: Koin
  private set

class PetalsApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    koin = startKoin {
      androidContext(this@PetalsApplication)
      modules(KoinModule)
      modules(AndroidModule)
      modules(IoModules)
      modules(SqlDelightModule)
    }.koin
    startTimber()
  }
}

private fun startTimber() {
  if (DEBUG) {
    Timber.plant(Timber.DebugTree())
  }
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
