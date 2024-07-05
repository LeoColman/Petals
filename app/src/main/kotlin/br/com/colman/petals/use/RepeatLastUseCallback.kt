package br.com.colman.petals.use

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import br.com.colman.petals.koin
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.widget.PetalsRepeatLastUseWidget
import br.com.colman.petals.widget.PetalsRepeatLastUseWidget.WidgetStateKey
import br.com.colman.petals.widget.WidgetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime.now

object RepeatLastUseCallback : ActionCallback {
  val useRepository: UseRepository = koin.get<UseRepository>()
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    useRepository.repeatLastUse()
    updateWidget(context, glanceId)
    checkLastUseMinuteAndUpdateWidget(context, glanceId)
  }

  private suspend fun updateWidget(context: Context, glanceId: GlanceId) {
    updateAppWidgetState(context, glanceId) { prefs ->
      val currentState = prefs[WidgetStateKey]?.let { WidgetState.valueOf(it) } ?: WidgetState.ENABLED
      prefs[WidgetStateKey] = currentState.changeState.name
    }
    PetalsRepeatLastUseWidget.updateAll(context)
  }

  private fun checkLastUseMinuteAndUpdateWidget(context: Context, glanceId: GlanceId) {
    val scope = CoroutineScope(Dispatchers.IO)

    scope.launch {
      val lastUseMinute = now().minute
      while (isActive) {
        if (lastUseMinute < now().minute) {
          updateWidget(context, glanceId)
          break
        }
        delay(15000)
      }
    }
  }
}
