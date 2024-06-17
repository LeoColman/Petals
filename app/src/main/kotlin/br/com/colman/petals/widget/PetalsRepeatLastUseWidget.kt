package br.com.colman.petals.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Alignment.Horizontal.Companion.CenterHorizontally
import androidx.glance.layout.Alignment.Vertical.Companion.CenterVertically
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import br.com.colman.petals.use.RepeatLastUseCallback

object PetalsRepeatLastUseWidget : GlanceAppWidget() {
  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      Column(
        GlanceModifier.fillMaxSize().background(Color(128, 128, 128, 80)),
        CenterVertically,
        CenterHorizontally,
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalAlignment = Alignment.CenterVertically,
          modifier = GlanceModifier.fillMaxWidth()
        ) {
          Text(
            text = "Quick use",
            modifier = GlanceModifier.padding(bottom = 6.dp),
            style = TextStyle(
              textAlign = TextAlign.Center,
              fontWeight = FontWeight.Medium,
              color = ColorProvider(Color.White),
              fontSize = 22.sp
            )
          )
          Button(
            text = "Repeat",
            style = TextStyle(
              fontWeight = FontWeight.Medium,
              fontSize = 16.sp,
            ),
            onClick = actionRunCallback(RepeatLastUseCallback::class.java)
          )
        }
      }
    }
  }
}

class PetalsRepeatLastUseWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget = PetalsRepeatLastUseWidget
}
