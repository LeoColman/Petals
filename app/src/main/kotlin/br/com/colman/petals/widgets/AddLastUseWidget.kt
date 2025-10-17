package br.com.colman.petals.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.material.ColorProviders
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import br.com.colman.petals.MainActivity
import br.com.colman.petals.PetalsApplication
import br.com.colman.petals.R
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.theme.darkColors
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import kotlinx.coroutines.flow.first
import org.koin.android.ext.android.getKoin
import org.koin.compose.koinInject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter.ofPattern

class AddLastUseWidget : GlanceAppWidget() {

  override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

  companion object {
    val USED_DATE = stringPreferencesKey("used_date")
    val USED_AMOUNT = stringPreferencesKey("used_amount")
  }

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val useRepository =
      (context.applicationContext as PetalsApplication).getKoin().get<UseRepository>()
    val lastUse = useRepository.getLastUse().first()

    updateAppWidgetState(context, id) { prefs ->
      if (lastUse != null) {
        prefs[USED_DATE] = lastUse.date.toString()
        prefs[USED_AMOUNT] = lastUse.amountGrams.toString()
      } else {
        prefs.remove(USED_DATE)
        prefs.remove(USED_AMOUNT)
      }
    }

    provideContent {
      val settingsRepository: SettingsRepository = koinInject()
      GlanceTheme(ColorProviders(darkColors)) {
        AddCopyOfLastUse(settingsRepository)
      }
    }
  }

  @Composable
  @OptIn(ExperimentalGlancePreviewApi::class)
  @Preview
  private fun AddCopyOfLastUse(settingsRepository: SettingsRepository) {
    val usedDateString = currentState(USED_DATE)
    val usedAmount = currentState(USED_AMOUNT)

    val dateFormat by settingsRepository.dateFormat.collectAsState(settingsRepository.dateFormatList[0])
    val timeFormat by settingsRepository.timeFormat.collectAsState(settingsRepository.timeFormatList[0])

    val context = LocalContext.current
    val addCopyOfMyLastUse = context.getString(R.string.add_a_copy_of_my_last_use)
    val addUse = context.getString(R.string.add_use)
    val amountGrams = context.getString(R.string.amount_with_grams)
    val dateResource = context.getString(R.string.date)

    val hasData = usedDateString != null && usedAmount != null

    Column(
      modifier = GlanceModifier
        .fillMaxSize()
        .background(
          ImageProvider(R.drawable.background_widget),
          colorFilter = ColorFilter.tint(
            colorProvider = ColorProvider(
              day = darkColors.background,
              night = darkColors.background
            )
          )
        )
        .padding(7.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      LastUsedDetailText()

      Spacer(modifier = GlanceModifier.height(8.dp))

      if (hasData) {
        UseDetails(dateResource, amountGrams, usedDateString, dateFormat, timeFormat)
      } else {
        NoUseFound()
      }

      Spacer(modifier = GlanceModifier.height(16.dp))

      Button(
        text = if (!hasData) addUse else addCopyOfMyLastUse,
        style = TextStyle(
          fontSize = 16.sp,
          fontWeight = FontWeight.Medium
        ),
        onClick = if (!hasData) actionStartActivity<MainActivity>() else actionRunCallback<AddUseActionCallback>()
      )
    }
  }

  @Composable
  private fun LastUsedDetailText() {
    val context = LocalContext.current
    val lastUsedDetails = context.getString(R.string.last_used_details)

    Text(
      text = lastUsedDetails,
      style = TextStyle(
        color = ColorProvider(
          day = darkColors.onBackground,
          night = darkColors.onBackground
        ),
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium
      )
    )
  }

  @Composable
  private fun NoUseFound() {
    val context = LocalContext.current
    val noPreviousUseFound = context.getString(R.string.no_previous_use_found)
    Text(
      text = noPreviousUseFound,
      style = TextStyle(
        color = ColorProvider(
          day = darkColors.onSurface.copy(alpha = 0.6f),
          night = darkColors.onSurface.copy(alpha = 0.6f)
        ),
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
      )
    )
  }

  @Composable
  private fun UseDetails(
    dateResource: String,
    amountGrams: String,
    usedDateString: String?,
    dateFormat: String,
    timeFormat: String
  ) {
    val usedAmount = currentState(USED_AMOUNT)

    val timeString = if (usedDateString != null) {
      formatDateTime(usedDateString, timeFormat)
    } else {
      ""
    }
    val dateString = if (usedDateString != null) {
      formatDateTime(usedDateString, dateFormat)
    } else {
      ""
    }
    val context = LocalContext.current
    val dateLabel = context.getString(R.string.date_at_time, dateString, timeString)

    Row(
      modifier = GlanceModifier.fillMaxWidth(),
      horizontalAlignment = Alignment.Start,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Spacer(modifier = GlanceModifier.width(8.dp))
      Text(
        text = "$dateResource: $dateLabel",
        style = TextStyle(
          color = ColorProvider(
            day = darkColors.onBackground,
            night = darkColors.onBackground
          ),
          fontSize = 14.sp,
          fontWeight = FontWeight.Normal
        )
      )
    }

    Spacer(modifier = GlanceModifier.height(6.dp))

    Row(
      modifier = GlanceModifier.fillMaxWidth(),
      horizontalAlignment = Alignment.Start,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Spacer(modifier = GlanceModifier.width(8.dp))
      Text(
        text = String.format(amountGrams, usedAmount),
        style = TextStyle(
          color = ColorProvider(
            day = darkColors.onBackground,
            night = darkColors.onBackground
          ),
          fontSize = 14.sp,
          fontWeight = FontWeight.Normal
        )
      )
    }
  }

  @Composable
  private fun formatDateTime(usedDateString: String, timeFormat: String): String? = try {
    val dateTime = LocalDateTime.parse(usedDateString)
    dateTime.format(ofPattern(timeFormat))
  } catch (_: Exception) {
    ""
  }
}

class AddUseActionCallback : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    val koin = (context.applicationContext as PetalsApplication).getKoin()
    val useRepository = koin.get<UseRepository>()

    val lastUse = useRepository.getLastUse().first()

    lastUse?.let { use ->
      val dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.now())
      val updatedUse = use.copy(date = dateTime)
      useRepository.upsert(updatedUse)

      context.updateWidget(updatedUse)
    }
  }
}

suspend fun Context.updateWidget(updatedUse: Use?) {
  val manager = GlanceAppWidgetManager(this)
  val widget = AddLastUseWidget()
  val glanceIds = manager.getGlanceIds(widget.javaClass)

  glanceIds.forEach { id ->

    updateAppWidgetState(this, id) { prefs ->
      if (updatedUse != null) {
        prefs[AddLastUseWidget.USED_DATE] = updatedUse.date.toString()
        prefs[AddLastUseWidget.USED_AMOUNT] = updatedUse.amountGrams.toString()
      } else {
        prefs.remove(AddLastUseWidget.USED_DATE)
        prefs.remove(AddLastUseWidget.USED_AMOUNT)
      }
    }
    widget.update(this, id)
  }
}
