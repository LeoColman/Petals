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
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import br.com.colman.petals.R.string.pin_main_screen
import br.com.colman.petals.navigation.BottomNavigationBar
import br.com.colman.petals.navigation.MyTopAppBar
import br.com.colman.petals.navigation.NavHostContainer
import java.time.LocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("FunctionName")
class MainActivity : ComponentActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

  private var authorizedUntil = LocalDateTime.MIN

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberNavController()

      var isAuthorized by remember { mutableStateOf(false) }

      LaunchedEffect(authorizedUntil) {
        while (true) {
          isAuthorized = isAuthorized()
          delay(1000)
        }
      }

      MaterialTheme(if (isSystemInDarkTheme()) darkColors() else lightColors()) {
        if (isAuthorized) {
          Surface {
            Scaffold(
              topBar = { MyTopAppBar(navController) },
              bottomBar = { BottomNavigationBar(navController) },
              content = { NavHostContainer(navController) }
            )
          }
        } else {
          Authorization()
        }
      }
    }
  }

  fun isAuthorized() = authorizedUntil >= LocalDateTime.now()

  @Composable
  fun Authorization() {
    var pin by remember { mutableStateOf("") }

    LaunchedEffect(pin) {
      if (pin == "abcd") {
        authorizedUntil = LocalDateTime.now().plusMinutes(30L)
      }
    }

    Column {
      Text(stringResource(pin_main_screen))
      OutlinedTextField(pin, { pin = it })
    }
  }
}
