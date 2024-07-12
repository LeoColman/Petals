package br.com.colman.petals.use

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import br.com.colman.petals.koin
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.widget.PetalsRepeatLastUseWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime.now

object RepeatLastUseCallback : ActionCallback {
  val useRepository: UseRepository = koin.get<UseRepository>()
  val scope = CoroutineScope(Dispatchers.IO)

  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    useRepository.repeatLastUse()
    checkLastUseMinuteAndUpdateWidget(context)
  }

  private fun checkLastUseMinuteAndUpdateWidget(context: Context) {
    scope.launch{
      val lastUseMinute = now().minute
      PetalsRepeatLastUseWidget.updateAll(context)
      while (isActive){
        if (lastUseMinute < now().minute) {
          PetalsRepeatLastUseWidget.updateAll(context)
          break
        }
        delay(10000)
      }
    }
  }
}
