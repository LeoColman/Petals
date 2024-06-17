package br.com.colman.petals.use

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import br.com.colman.petals.koin
import br.com.colman.petals.use.repository.UseRepository

object RepeatLastUseCallback : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    val useRepository: UseRepository = koin.get<UseRepository>()

    useRepository.repeatLastUse()
  }
}
