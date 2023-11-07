package br.com.colman.petals.widget

import android.content.Context
import androidx.compose.material.MaterialTheme.colors
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize

object PetalsAppWidget : GlanceAppWidget() {

  override suspend fun provideGlance(context: Context, id: GlanceId) {

    provideContent {
      val colors = colors
      Column(
        modifier = GlanceModifier.fillMaxSize().background(colors.onBackground),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
      ) {
        WidgetUsagePart()
        WidgetConcentrationDiscomfortPart()
        WidgetBreakPeriodPart()
      }
    }
  }
}

class SimplePetalsAppWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget
    get() = PetalsAppWidget
}
