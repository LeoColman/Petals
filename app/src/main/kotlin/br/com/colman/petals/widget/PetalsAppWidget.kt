package br.com.colman.petals.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.Button

import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
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

object PetalsAppWidget : GlanceAppWidget() {

  val countKey = intPreferencesKey("count");

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val count = currentState(key = countKey) ?: 0
      Column(
        modifier = GlanceModifier.fillMaxSize().background(Color.DarkGray),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
      ) {
        Text(
          text = count.toString(),
          style = TextStyle(
            fontWeight = FontWeight.Medium,
            color = ColorProvider(Color.White),
            fontSize = 26.sp
          )
        )
        Button(
          text = "Inc",
          onClick = actionRunCallback(IncrementActionCallback::class.java)
        )
      }
    }
  }
}

class SimplePetalsAppWidgetReceiver : GlanceAppWidgetReceiver(){
  override val glanceAppWidget: GlanceAppWidget
    get() = PetalsAppWidget
}

class IncrementActionCallback: ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    updateAppWidgetState(context, glanceId){ prefs ->
      val currentCount = prefs[PetalsAppWidget.countKey]
      if (currentCount != null){
        prefs[PetalsAppWidget.countKey] = currentCount + 1
      } else {
        prefs[PetalsAppWidget.countKey] = 1
      }
    PetalsAppWidget.update(context,glanceId)
    }
  }
}
