package br.com.colman.petals.playstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import br.com.colman.petals.navigation.BottomNavigationBar
import br.com.colman.petals.navigation.MyTopAppBar
import br.com.colman.petals.navigation.NavHostContainer
import br.com.colman.petals.settings.SettingsRepository
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@Suppress("FunctionName")
class MainActivity : ComponentActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

  private val settingsRepository by inject<SettingsRepository>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberNavController()

      MaterialTheme(if (isDarkModeEnabled()) darkColors() else lightColors()) {
        Surface {
          Scaffold(
            topBar = { MyTopAppBar(navController) },
            bottomBar = {
              Column {
                BottomNavigationBar(navController)
                AdsView()
              }
            },
            content = { NavHostContainer(navController, it) }
          )
        }
      }
    }
    launch { MobileAds.initialize(this@MainActivity) }
  }

  @Composable
  fun isDarkModeEnabled(): Boolean {
    val darkMode: Boolean? by settingsRepository.isDarkModeEnabled.collectAsState(null)
    return darkMode ?: isSystemInDarkTheme()
  }
}

