package br.com.colman.petals.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import br.com.colman.petals.R
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.TimeUnit
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.utils.truncatedToMinute
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@Composable
fun WidgetUsagePart() {
  val settingsRepository = koinInject<SettingsRepository>()
  val useRepository: UseRepository = koinInject()
  val lastUseDate = useRepository.getLastUseDate().collectAsState(LocalDateTime.now())
  val dateFormat by settingsRepository.dateFormat.collectAsState(settingsRepository.dateFormatList[0])
  val timeFormat by settingsRepository.timeFormat.collectAsState(settingsRepository.timeFormatList[0])
  val millisecondsEnabled by settingsRepository.millisecondsEnabled.collectAsState(false)
  val dateString =
    DateTimeFormatter.ofPattern(String.format(Locale.US, "%s %s", dateFormat, timeFormat))
      .format(lastUseDate.value)
  var millis by remember {
    mutableStateOf(
      ChronoUnit.MILLIS.between(
        lastUseDate.value,
        LocalDateTime.now()
      )
    )
  }

  LaunchedEffect(millis) {
    while (true) {
      delay(11)
      millis = ChronoUnit.MILLIS.between(lastUseDate.value, LocalDateTime.now())
    }
  }

  val allLabels = listOf(
    TimeUnit.Year,
    TimeUnit.Month,
    TimeUnit.Day,
    TimeUnit.Hour,
    TimeUnit.Minute,
    TimeUnit.Second,
    TimeUnit.Millisecond
  )

  val enabledLabels = if (millisecondsEnabled == "disabled") allLabels.dropLast(1) else allLabels

  var millisCopy = millis
  val labels = enabledLabels.map {
    val unitsTotal = millisCopy / it.millis
    millisCopy -= unitsTotal * it.millis
    it to unitsTotal
  }

  Column(
    modifier = GlanceModifier.padding(4.dp),
    verticalAlignment = Alignment.Vertical.CenterVertically,
    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
  ) {
    LastUseDateView(lastUseDate, dateString)
    Column(
      verticalAlignment = Alignment.CenterVertically,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      YearsMonthsDaysView(labels)
      HoursMinutesSecondsView(labels)
    }
  }
}

@Composable
private fun LastUseDateView(
  lastUseDate: State<LocalDateTime?>,
  dateString: String?
) {
  Column(
    verticalAlignment = Alignment.CenterVertically,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = LocalContext.current.getString(R.string.quit_date_text),
      style = TextStyle(
        fontWeight = FontWeight.Medium,
        color = ColorProvider(Color.White),
        fontSize = 20.sp
      )
    )
    val dateStringWithExtras = if (!lastUseDate.value!!.is420()) dateString else "$dateString 🥦🥦"
    Text(
      text = dateStringWithExtras!!,
      style = TextStyle(
        fontWeight = FontWeight.Medium,
        color = ColorProvider(Color.White),
        fontSize = 20.sp
      )
    )
  }
}

@Composable
private fun YearsMonthsDaysView(labels: List<Pair<TimeUnit, Long>>) {
  Row(
    modifier = GlanceModifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    labels.filter { it.first in listOf(TimeUnit.Year, TimeUnit.Month, TimeUnit.Day) }
      .forEach { (label, amount) ->
        Row {
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
}

@Composable
private fun HoursMinutesSecondsView(labels: List<Pair<TimeUnit, Long>>) {
  Row(
    modifier = GlanceModifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    labels.filter { it.first in listOf(TimeUnit.Hour, TimeUnit.Minute, TimeUnit.Second) }
      .forEach { (label, amount) ->
        Row {
          if (LocalContext.current.getString(label.unitName) != "Hours") {
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
              text = "$amount",
              style = TextStyle(
                fontWeight = FontWeight.Normal,
                color = ColorProvider(Color.White),
                fontSize = 16.sp
              )
            )
          }
          if (LocalContext.current.getString(label.unitName) != "Seconds") {
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

private fun LocalDateTime.is420() = toLocalTime().truncatedToMinute() == LocalTime.of(16, 20)

private fun formatLongAsTwoDigitString(input: Long): String {
  return String.format(Locale.US, "%02d", input)
}
