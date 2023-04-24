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
import br.com.colman.petals.use.pause.repository.PauseRepository
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.discomfort.repository.DiscomfortRepository
import br.com.colman.petals.withdrawal.discomfort.view.DiscomfortView
import br.com.colman.petals.withdrawal.interpolator.DiscomfortInterpolator
import br.com.colman.petals.withdrawal.interpolator.ThcConcentrationInterpolator
import br.com.colman.petals.withdrawal.thc.repository.ThcConcentrationRepository
import br.com.colman.petals.withdrawal.thc.view.ThcConcentrationView
import org.koin.dsl.module

private val Context.settingsDatastore by preferencesDataStore("settings")

val KoinModule = module {
  single { UseRepository(get<Database>().useQueries) }
  single { PauseRepository(get<Database>().pauseQueries) }
  single { HitTimerRepository(get()) }
  single { SettingsRepository(get<Context>().settingsDatastore) }

  single { ThcConcentrationInterpolator() }
  single { ThcConcentrationRepository(get(), get()) }
  single { ThcConcentrationView(get(), get()) }

  single { DiscomfortInterpolator() }
  single { DiscomfortRepository(get(), get()) }
  single { DiscomfortView(get(), get()) }
}
