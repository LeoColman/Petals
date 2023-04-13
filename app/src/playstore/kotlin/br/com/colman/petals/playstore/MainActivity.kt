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
import androidx.navigation.compose.rememberNavController
import br.com.colman.petals.navigation.BottomNavigationBar
import br.com.colman.petals.navigation.MyTopAppBar
import br.com.colman.petals.navigation.NavHostContainer
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
}