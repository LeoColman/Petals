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
import br.com.colman.petals.BuildConfig.DEBUG
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.io.output.auto.AutoExportScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin
import timber.log.Timber
import java.io.IOException

lateinit var koin: Koin
  private set

class PetalsApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    startKoin()
    startTimber()
    rescheduleAutoExportIfEnabled()
  }

  private fun startKoin() {
    koin = startKoin {
      androidContext(this@PetalsApplication)
      modules(KoinModule)
    }.koin
  }

  private fun startTimber() {
    if (DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  /**
   * Belt-and-braces for the force-stop case: WorkManager's own reboot
   * rescheduling only fires on BOOT_COMPLETED, not on a plain force-stop.
   * KEEP makes schedule() idempotent, and this runs off the main thread
   * since DataStore reads are disk IO.
   */
  private fun rescheduleAutoExportIfEnabled(dispatcher: CoroutineDispatcher = IO) {
    // This is a root coroutine: nothing is above it to catch anything it throws, so an
    // exception here would reach the default handler and take the process down AT APP
    // START. Failing to re-schedule a background export must never do that — the next
    // launch (or WorkManager's own reboot rescheduling) gets another go.
    CoroutineScope(dispatcher).launch {
      try {
        val settingsRepository = koin.get<SettingsRepository>()
        if (settingsRepository.isAutoExportEnabled.first()) {
          koin.get<AutoExportScheduler>().schedule()
        }
      } catch (e: IOException) {
        Timber.w(e, "Could not read auto-export settings to reschedule")
      } catch (e: IllegalStateException) {
        Timber.w(e, "Could not reschedule auto-export")
      }
    }
  }
}
