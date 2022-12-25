package br.com.colman.petals.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import androidx.glance.text.Text
import br.com.colman.petals.initializeKoin
import br.com.colman.petals.use.repository.UseRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import java.time.LocalDateTime

class HelloWorldWidget : GlanceAppWidget() {

  private val coroutineScope = MainScope()
  var data by mutableStateOf<LocalDateTime?>(null)

  @Composable
  override fun Content() {
    val context = LocalContext.current
    LocalContext.current.initializeKoin()
    val lastUseRepository = get<UseRepository>()
    coroutineScope.launch {
      data = lastUseRepository.getLastUseDate().first()
      updateAll(context)
    }

    Text(text = "$data")
  }
}

class HelloWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget = HelloWorldWidget()
}
