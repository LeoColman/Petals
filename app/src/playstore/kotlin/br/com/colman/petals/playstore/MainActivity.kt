package br.com.colman.petals.playstore


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import br.com.colman.petals.koin
import br.com.colman.petals.navigation.BottomNavigationBar
import br.com.colman.petals.navigation.MyTopAppBarContent
import br.com.colman.petals.navigation.NavHostContainer
import br.com.colman.petals.settings.SettingsMigrations
import br.com.colman.petals.settings.SettingsRepository
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.dsl.module

private val AppPurchaseModule = module {
  single {
    InAppPurchaseUtil(get<Context>())
  }
}

@Suppress("FunctionName")
class MainActivity : ComponentActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

  private val adsSettingsRepository by inject<AdsSettingsRepository>()
  private val settingsRepository by inject<SettingsRepository>()
  private val settingsMigrations by inject<SettingsMigrations>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      settingsMigrations.migrateOldKeysValues()
      settingsMigrations.removeOldKeysValues()
      koin.loadModules(listOf(
        AppPurchaseModule
      ))
      val navController = rememberNavController()
      MaterialTheme(if (isDarkModeEnabled()) darkColors() else lightColors()) {
        Surface {
          Scaffold(
            topBar = {
              TopAppBar{
                 MyTopAppBarContent(navController)
                 AdFreeListItem()
              } },
            bottomBar = {
              Column {
                BottomNavigationBar(navController)
                if(!isAdFree()){
                  AdsView()
                }
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

  @Composable
  fun isAdFree(): Boolean {
    return adsSettingsRepository.isAdsFree.collectAsState(false).value
  }
}
