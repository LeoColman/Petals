package br.com.colman.petals.widgets

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class AddLastUseWidgetReciever : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = AddLastUseWidget()
}
