package br.com.colman.petals.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import br.com.colman.petals.use.pause.repository.PauseRepository
import org.koin.androidx.compose.get

@Composable
fun WidgetBreakPeriodPart() {
  val pauseRepository: PauseRepository = get()
  val pause by pauseRepository.get().collectAsState(null)
  var isPause = true
  if (pause == null || !pause!!.isActive()) {
    isPause = false
  }

  Column(
    modifier = GlanceModifier.padding(4.dp),
    verticalAlignment = Alignment.Vertical.CenterVertically,
    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
  ) {
    if (isPause) {
      Text(
        text = "Break period",
        style = TextStyle(
          fontWeight = FontWeight.Bold,
          color = ColorProvider(Color.Red),
          fontSize = 18.sp
        )
      )
    } else {
      Text(
        text = "No break period",
        style = TextStyle(
          fontWeight = FontWeight.Bold,
          color = ColorProvider(Color.Green),
          fontSize = 18.sp
        )
      )
    }
  }
}
