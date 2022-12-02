package br.com.colman.petals.widget

import androidx.compose.runtime.Composable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.text.Text

class HelloWorldWidget : GlanceAppWidget() {

  @Composable
  override fun Content() {
    Text(text = "Hello, world!")
  }
}

class HelloWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget = HelloWorldWidget()
}
