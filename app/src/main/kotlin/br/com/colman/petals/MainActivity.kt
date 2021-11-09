package br.com.colman.petals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.clock.QuitTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.joda.time.Duration
import org.joda.time.LocalDateTime
import org.joda.time.LocalDateTime.now
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

   private val quitTimer by inject<QuitTimer>()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {

         Column {
            QuitButton()

            val quitTime by quitTimer.quitDate.collectAsState(null)
            QuitTimerText(quitTime)
         }
      }
   }

   @Preview
   @Composable
   fun QuitButton() {
      Button({ quit() }) {
         Text("Quit")
      }
   }

   private fun quit() {
      launch { quitTimer.setQuitDate(now()) }
   }

   @Preview
   @Composable
   fun QuitTimerText(stopDate: LocalDateTime? = now()) {
      if (stopDate == null) {
         Text("You haven't stopped yet.")
         return
      }

      var secondsFromStopDate by remember { mutableStateOf(Duration(stopDate.toDateTime(), now().toDateTime())) }
      LaunchedEffect(true) {
         while(true) {
            delay(1_000)
            secondsFromStopDate = Duration(stopDate.toDateTime(), now().toDateTime())
         }
      }

      Column {
         Text("Time since you've quit")
         Text("${secondsFromStopDate.standardSeconds} seconds")
      }
   }

}
