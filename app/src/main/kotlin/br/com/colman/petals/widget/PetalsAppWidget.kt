package br.com.colman.petals.widget

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment.Horizontal.Companion.CenterHorizontally
import androidx.glance.layout.Alignment.Vertical.Companion.CenterVertically
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize

object PetalsAppWidget : GlanceAppWidget() {
  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      Column(
        GlanceModifier.fillMaxSize().background(MaterialTheme.colorScheme.onBackground),
        CenterVertically,
        CenterHorizontally
      ) {
        WidgetUsagePart()
        WidgetConcentrationDiscomfortPart()
        BreakPeriodPart()
      }
    }
  }
}

class PetalsAppWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget = PetalsAppWidget
}
