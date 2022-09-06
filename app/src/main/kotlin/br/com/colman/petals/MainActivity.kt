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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.navigation.compose.rememberNavController
import br.com.colman.petals.navigation.BottomNavigationBar
import br.com.colman.petals.navigation.MyTopAppBar
import br.com.colman.petals.navigation.NavHostContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Suppress("FunctionName")
class MainActivity : ComponentActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberNavController()

      MaterialTheme(if (isSystemInDarkTheme()) darkColors() else lightColors()) {
        Surface {
          Scaffold(
            topBar = { MyTopAppBar(navController) },
            bottomBar = { BottomNavigationBar(navController) },
            content = { NavHostContainer(navController) }
          )
        }
      }
    }
  }
}
