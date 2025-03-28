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
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import br.com.colman.petals.R.string.pin_main_screen
import br.com.colman.petals.navigation.BottomNavigationBar
import br.com.colman.petals.navigation.MyTopAppBar
import br.com.colman.petals.navigation.NavHostContainer
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.theme.darkColors
import br.com.colman.petals.theme.lightColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject
import java.time.LocalDateTime

class MainActivity : AppCompatActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

  private var authorizedUntil = LocalDateTime.MIN
  private val settingsRepository by inject<SettingsRepository>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberNavController()

      var isAuthorized by remember { mutableStateOf(false) }
      val correctPin by settingsRepository.pin.collectAsState(null)

      LaunchedEffect(authorizedUntil) {
        while (true) {
          isAuthorized = isAuthorized()
          delay(1000)
        }
      }

      MaterialTheme(getAppColors()) {
        if (isAuthorized || correctPin == null) {
          Surface {
            Scaffold(
              topBar = { MyTopAppBar(navController) },
              bottomBar = { BottomNavigationBar(navController) },
              content = { NavHostContainer(navController, it) }
            )
          }
        } else {
          Authorization(correctPin)
        }
      }
    }
  }

  private fun isAuthorized() = authorizedUntil >= LocalDateTime.now()

  @Composable
  fun Authorization(correctPin: String?) {
    var pin by remember { mutableStateOf("") }

    LaunchedEffect(pin) {
      if (pin == correctPin) {
        authorizedUntil = LocalDateTime.now().plusMinutes(30L)
      }
    }

    Column(Modifier.padding(16.dp)) {
      Text(stringResource(pin_main_screen))
      OutlinedTextField(pin, { pin = it }, visualTransformation = PasswordVisualTransformation())
    }
  }

  @Composable
  fun getAppColors(): Colors = if (isDarkModeEnabled()) darkColors else lightColors

  @Composable
  fun isDarkModeEnabled(): Boolean {
    val darkMode: Boolean by settingsRepository.isDarkModeEnabled.collectAsState(isSystemInDarkTheme())
    return darkMode
  }
}
