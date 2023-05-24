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

package br.com.colman.petals.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import br.com.colman.petals.R
import br.com.colman.petals.hittimer.ComposeHitTimer
import br.com.colman.petals.navigation.Page.Usage
import br.com.colman.petals.settings.SettingsView
import br.com.colman.petals.statistics.StatisticsPage
import br.com.colman.petals.withdrawal.Symptoms
import org.koin.androidx.compose.get

enum class Page(
  @StringRes val nameRes: Int,
  val icon: ImageVector,
  val ui: @Composable () -> Unit
) {
  Usage(R.string.usage, Default.SmokingRooms, { Usage() }),
  HitTimer(R.string.hit_timer, Default.LockClock, { ComposeHitTimer() }),
  Symptoms(R.string.symptoms, Default.MedicalServices, { Symptoms() }),
  Stats(R.string.stats, Default.GraphicEq, { StatisticsPage(get(), get()) })
}

@Composable
fun NavHostContainer(navController: NavHostController, paddingValues: PaddingValues) {
  NavHost(navController, Usage.name, Modifier.padding(paddingValues)) {
    Page.values().forEach { page ->
      composable(page.name) {
        page.ui()
      }
    }

    composable("settings") {
      SettingsView(get())
    }
  }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  BottomNavigation {
    Page.values().forEach { page ->
      BottomNavigationItem(
        selected = currentRoute == page.name,

        onClick = {
          navController.navigate(page.name)
        },

        icon = { Icon(page.icon, stringResource(page.nameRes)) },

        label = { Text(stringResource(page.nameRes)) }
      )
    }
  }
}
