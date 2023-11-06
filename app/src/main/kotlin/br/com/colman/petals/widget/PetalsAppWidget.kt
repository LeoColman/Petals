package br.com.colman.petals.widget

import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import br.com.colman.petals.R.string.quit_date_text
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
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import br.com.colman.petals.use.pause.repository.PauseRepository
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.utils.truncatedToMinute
import kotlinx.coroutines.delay
import java.time.LocalDateTime

import org.koin.androidx.compose.get
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object PetalsAppWidget : GlanceAppWidget() {

  val secondsFromLastUseKey = intPreferencesKey("secondsFromLastUse")

  override suspend fun provideGlance(context: Context, id: GlanceId) {

    provideContent {
      val settingsRepository = get<SettingsRepository>()
      val useRepository: UseRepository = get()
      val pauseRepository: PauseRepository = get()
      val lastUseDateState = remember { mutableStateOf<LocalDateTime?>(null) }
      val dateFormat by settingsRepository.dateFormat.collectAsState(settingsRepository.dateFormatList[0])
      val timeFormat by settingsRepository.timeFormat.collectAsState(settingsRepository.timeFormatList[0])
      val isInitialDataFetched = remember { mutableStateOf(false) }
      val millisecondsEnabled = "disabled"

      LaunchedEffect(isInitialDataFetched.value) {
        useRepository.getLastUseDate().collect { lastUseDate ->
          lastUseDateState.value = lastUseDate
          isInitialDataFetched.value = true
        }
      }

      val lastUseDate = lastUseDateState.value ?: LocalDateTime.now()

      val dateString = DateTimeFormatter.ofPattern(
        String.format(
          Locale.US,
          "%s %s",
          dateFormat,
          timeFormat
        )
      ).format(lastUseDate)

      var millis by remember {
        mutableStateOf(
          ChronoUnit.MILLIS.between(
            lastUseDate,
            LocalDateTime.now()
          )
        )
      }
      LaunchedEffect(millis) {
        while (true) {
          delay(11)
          millis = ChronoUnit.MILLIS.between(lastUseDate, LocalDateTime.now())
        }
      }
      val allLabels = listOf(Year, Month, Day, Hour, Minute, Second, Millisecond)
      val enabledLabels =
        if (millisecondsEnabled == "disabled") allLabels.dropLast(1) else allLabels


      var millisCopy = millis
      val labels = enabledLabels.map {
        val unitsTotal = millisCopy / it.millis
        millisCopy -= unitsTotal * it.millis
        it to unitsTotal
      }

      val count = currentState(key = secondsFromLastUseKey) ?: 0
      Column(
        modifier = GlanceModifier.fillMaxSize().background(Color.DarkGray),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
      ) {
        Column(
          verticalAlignment = Alignment.CenterVertically,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = LocalContext.current.getString(quit_date_text),
            style = TextStyle(
              fontWeight = FontWeight.Medium,
              color = ColorProvider(Color.White),
              fontSize = 20.sp
            )
          )
          val dateStringWithExtras = if (!lastUseDate.is420()) dateString else "$dateString 🥦🥦"
          Text(
            text = dateStringWithExtras,
            style = TextStyle(
              fontWeight = FontWeight.Medium,
              color = ColorProvider(Color.White),
              fontSize = 20.sp
            )
          )
        }

        Column(
          verticalAlignment = Alignment.CenterVertically,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Years, Months, and Days
          Column(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Years, Months, and Days
            Row(
              modifier = GlanceModifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              labels.filter { it.first in listOf(Year, Month, Day) }
                .forEach { (label, amount) ->
                  Row() {
                    Text(
                      text = LocalContext.current.getString(label.unitName),
                      style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        color = ColorProvider(Color.White),
                        fontSize = 16.sp
                      )
                    )
                    Text(
                      text = ": $amount ",
                      style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        color = ColorProvider(Color.White),
                        fontSize = 16.sp
                      )
                    )
                  }
                }
            }
            // Hours, Minutes, and Seconds
            Row(
              modifier = GlanceModifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              labels.filter { it.first in listOf(Hour, Minute, Second) }
                .forEach { (label, amount) ->
                  Row() {
                    if (!LocalContext.current.getString(label.unitName).equals("Hours")) {
                      Text(
                        text = formatLongAsTwoDigitString(amount),
                        style = TextStyle(
                          fontWeight = FontWeight.Normal,
                          color = ColorProvider(Color.White),
                          fontSize = 16.sp
                        )
                      )
                    } else {
                      Text(
                        text = "$amount", style = TextStyle(
                          fontWeight = FontWeight.Normal,
                          color = ColorProvider(Color.White),
                          fontSize = 16.sp
                        )
                      )
                    }
                    if (!LocalContext.current.getString(label.unitName).equals("Seconds")) {
                      Text(
                        text = ":",
                        style = TextStyle(
                          fontWeight = FontWeight.Normal,
                          color = ColorProvider(Color.White),
                          fontSize = 16.sp
                        )
                      )
                    }
                  }
                }
            }
          }
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

  private fun LocalDateTime.is420() = toLocalTime().truncatedToMinute() == LocalTime.of(16, 20)

  fun formatLongAsTwoDigitString(input: Long): String {
    return String.format("%02d", input)
  }
}
