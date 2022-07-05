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

import br.com.colman.petals.hittimer.HitTimerRepository
import br.com.colman.petals.use.repository.MyObjectBox
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.discomfort.repository.DiscomfortRepository
import br.com.colman.petals.withdrawal.discomfort.view.DiscomfortView
import br.com.colman.petals.withdrawal.thc.repository.ThcConcentrationRepository
import br.com.colman.petals.withdrawal.thc.view.ThcConcentrationView
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val KoinModule = module {
  single { MyObjectBox.builder().androidContext(androidApplication()).build() }
  single { UseRepository(get<BoxStore>().boxFor()) }
  single { HitTimerRepository(get()) }

  single { ThcConcentrationRepository(get()) }
  single { ThcConcentrationView(get(), get()) }

  single { DiscomfortRepository(get()) }
  single { DiscomfortView(get(), get()) }
}
