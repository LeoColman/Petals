package br.com.colman.petals.use

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import br.com.colman.petals.R
import br.com.colman.petals.koin
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.widget.PetalsRepeatLastUseWidget
import br.com.colman.petals.widget.WidgetRepository
import br.com.colman.petals.widget.WidgetRepository.Companion.IconKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
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
    updateAppWidgetState(context, glanceId){prefs ->
      val currentIcon = prefs[PetalsRepeatLastUseWidget.iconKey]
      if(currentIcon == R.drawable.ic_repeat || currentIcon == null) {
        prefs[PetalsRepeatLastUseWidget.iconKey] = R.drawable.ic_padlock
      } else if (currentIcon == R.drawable.ic_padlock && useRepository.getLastUse().firstOrNull()?.date?.minute != now().minute)  {
        prefs[PetalsRepeatLastUseWidget.iconKey] = R.drawable.ic_repeat
      }
    }
    PetalsRepeatLastUseWidget.updateAll(context)
  }

  private fun checkLastUseMinuteAndUpdateWidget(context: Context, glanceId: GlanceId) {
    val job = Job()
    val scope = CoroutineScope(Dispatchers.IO + job)

    scope.launch() {
      while (isActive) {
        val lastUsage = useRepository.getLastUse().firstOrNull()
        val now = now().minute
        if (lastUsage?.date?.minute != now) {
          updateWidget(context, glanceId)
          break
        }
        delay(15000)
      }
    }
  }
}
