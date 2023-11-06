package br.com.colman.petals.widget

import android.content.Context
import android.os.Looper
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.Button

import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.colman.petals.R
import br.com.colman.petals.use.pause.repository.PauseRepository
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.utils.truncatedToMinute
import kotlinx.coroutines.delay
import java.time.LocalDateTime

import org.koin.androidx.compose.get
import java.time.LocalTime
import java.time.temporal.ChronoUnit

object PetalsAppWidget : GlanceAppWidget() {

  val secondsFromLastUseKey = intPreferencesKey("secondsFromLastUse")

  override suspend fun provideGlance(context: Context, id: GlanceId) {

    provideContent {
      val lastUseDateState = remember { mutableStateOf<LocalDateTime?>(null) }
      val useRepository: UseRepository = get()
      val pauseRepository: PauseRepository = get()
      val isInitialDataFetched = remember { mutableStateOf(false) }

      LaunchedEffect(isInitialDataFetched.value) {
        useRepository.getLastUseDate().collect { lastUseDate ->
          lastUseDateState.value = lastUseDate
          isInitialDataFetched.value = true
        }
      }

      LaunchedEffect(isInitialDataFetched.value) {
        while (true) {
          if (isInitialDataFetched.value) {
            val lastUseDate = lastUseDateState.value
            val secondsFromLastUse = lastUseDate?.let { ChronoUnit.SECONDS.between(it, LocalDateTime.now()) } ?: 0
            updateWidget(context, id, secondsFromLastUse.toInt())
          }
          // Delay for one second before the next update
          delay(1000)
        }
      }


      val count = currentState(key = secondsFromLastUseKey) ?: 0
      Column(
        modifier = GlanceModifier.fillMaxSize().background(Color.DarkGray),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
      ) {
        // Display your widget content here
        Text(text = count.toString())
      }
//        Button(
//          text = "Inc",
//          onClick = actionRunCallback(IncrementActionCallback::class.java)
//        )
    }
  }

  private suspend fun updateWidget(context: Context, glanceId: GlanceId, secondsFromLastUse: Int) {
    updateAppWidgetState(context, glanceId) { prefs ->
      val currentCount = prefs[PetalsAppWidget.secondsFromLastUseKey]
      if (currentCount != null) {
        prefs[PetalsAppWidget.secondsFromLastUseKey] = currentCount + 1
      } else {
        prefs[PetalsAppWidget.secondsFromLastUseKey] = secondsFromLastUse
      }
    }
    PetalsAppWidget.update(context, glanceId)
  }


}


class SimplePetalsAppWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget
    get() = PetalsAppWidget
}

class IncrementActionCallback: ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    updateAppWidgetState(context, glanceId){ prefs ->
      val currentCount = prefs[PetalsAppWidget.countKey]
      if (currentCount != null){
        prefs[PetalsAppWidget.countKey] = currentCount + 1
      } else {
        prefs[PetalsAppWidget.countKey] = 1
      }
    PetalsAppWidget.update(context,glanceId)
    }
  }
}
