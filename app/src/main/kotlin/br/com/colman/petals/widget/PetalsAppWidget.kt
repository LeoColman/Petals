package br.com.colman.petals.widget

import android.util.Log
import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.preference.PreferenceManager
import android.widget.RemoteViews
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.TimeUnit.Day
import br.com.colman.petals.use.TimeUnit.Hour
import br.com.colman.petals.use.TimeUnit.Millisecond
import br.com.colman.petals.use.TimeUnit.Minute
import br.com.colman.petals.use.TimeUnit.Month
import br.com.colman.petals.use.TimeUnit.Second
import br.com.colman.petals.use.TimeUnit.Year
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
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
import br.com.colman.petals.R
import br.com.colman.petals.use.pause.repository.PauseRepository
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.discomfort.repository.DiscomfortRepository
import br.com.colman.petals.withdrawal.thc.repository.ThcConcentrationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

import org.koin.androidx.compose.get
import timber.log.Timber
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.log

object PetalsAppWidget : GlanceAppWidget() {

  val millishaha = longPreferencesKey("millishaha")


  override suspend fun provideGlance(context: Context, id: GlanceId) {

    provideContent {
      val colors = colors
      val settingsRepository = get<SettingsRepository>()
      val useRepository: UseRepository = get()

//      val lastUseDateState = remember { mutableStateOf<LocalDateTime?>(null) }
      val lastUseDate = useRepository.getLastUseDate().collectAsState(LocalDateTime.now())
      val dateFormat by settingsRepository.dateFormat.collectAsState(settingsRepository.dateFormatList[0])
      val timeFormat by settingsRepository.timeFormat.collectAsState(settingsRepository.timeFormatList[0])

//      val isInitialDataFetched = remember { mutableStateOf(false) }
      val millisecondsEnabled = "disabled"

//      LaunchedEffect(isInitialDataFetched.value) {
//        useRepository.getLastUseDate().collect { lastUseDate ->
//          lastUseDateState.value = lastUseDate
//          isInitialDataFetched.value = true
//        }
//      }

//      val lastUseDate = lastUseDateState.value ?: LocalDateTime.now()

      val dateString = DateTimeFormatter.ofPattern(
        String.format(
          Locale.US, "%s %s", dateFormat, timeFormat
        )
      ).format(lastUseDate.value)

      var millis by remember {
        mutableStateOf(
          ChronoUnit.MILLIS.between(
            lastUseDate.value, LocalDateTime.now()
          )
        )
      }

      LaunchedEffect(millis) {
        while (true) {
          delay(11)
          millis = ChronoUnit.MILLIS.between(lastUseDate.value, LocalDateTime.now())
        }
      }

      Timber.tag("millis: ").d(millis.toString())

      val allLabels = listOf(Year, Month, Day, Hour, Minute, Second, Millisecond)
      val enabledLabels =
        if (millisecondsEnabled == "disabled") allLabels.dropLast(1) else allLabels


      var millisCopy = millis
      val labels = enabledLabels.map {
        val unitsTotal = millisCopy / it.millis
        millisCopy -= unitsTotal * it.millis
        it to unitsTotal
      }

      Column(
        modifier = GlanceModifier.fillMaxSize().background(colors.onBackground),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
      ) {
        WidgetUsagePart(lastUseDate.value, dateString, labels)
        WidgetConcentrationDiscomfortPart()
        WidgetBreakPeriodPart()
        Column {

        }
      }
    }
  }


//  private suspend fun updateWidget(
//    context: Context,
//    glanceId: GlanceId,
//    secondsFromLastUse: Int
//  ) {
//    updateAppWidgetState(context, glanceId) { prefs ->
//      val currentCount = prefs[PetalsAppWidget.secondsFromLastUseKey]
//      if (currentCount != null) {
//        prefs[PetalsAppWidget.secondsFromLastUseKey] = currentCount + 1
//      } else {
//        prefs[PetalsAppWidget.secondsFromLastUseKey] = secondsFromLastUse
//      }
//    }
//    PetalsAppWidget.update(context, glanceId)
//  }
//}

}

class SimplePetalsAppWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget
    get() = PetalsAppWidget
}


//class IncrementActionCallback : ActionCallback {
//  override suspend fun onAction(
//    context: Context,
//    glanceId: GlanceId,
//    parameters: ActionParameters
//  ) {
//
//    // Update the widget every second
//    updateAppWidgetState(context, glanceId) { prefs ->
//      val currentCount = prefs[PetalsAppWidget.secondsFromLastUseKey]
//      if (currentCount != null) {
//        prefs[PetalsAppWidget.secondsFromLastUseKey] = currentCount + 1
//      } else {
//        prefs[PetalsAppWidget.secondsFromLastUseKey] = 1
//      }
//      // Trigger the widget update
//      PetalsAppWidget.update(context, glanceId)
//    }
//  }
//}
//
