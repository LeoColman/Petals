package br.com.colman.petals.widget

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import br.com.colman.petals.R
import br.com.colman.petals.withdrawal.discomfort.repository.DiscomfortRepository
import br.com.colman.petals.withdrawal.thc.repository.ThcConcentrationRepository
import kotlinx.coroutines.flow.map
import org.koin.androidx.compose.get

@Composable
@SuppressLint("StringFormatInvalid", "FlowOperatorInvokedInComposition")
fun WidgetConcentrationDiscomfortPart() {
  val thcConcentrationRepository: ThcConcentrationRepository = get()
  val discomfortRepository: DiscomfortRepository = get()

  val currentPercentageTHC by thcConcentrationRepository.concentration.map { it * 100 }
    .collectAsState(100.0)
  val currentDiscomfort by discomfortRepository.discomfort.map { it.strength }
    .collectAsState(8.0)

  val thcConcentrationString = LocalContext.current.getString(
    R.string.current_thc_concentration,
    "%.3f".format(currentPercentageTHC)
  )
  val discomfortString = LocalContext.current.getString(
    R.string.current_withdrawal_discomfort,
    "%.3f".format(currentDiscomfort)
  )

  Column(
    modifier = GlanceModifier.padding(4.dp),
    verticalAlignment = Alignment.Vertical.CenterVertically,
    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
  ) {
    Text(
      text = thcConcentrationString,
      style = TextStyle(
        fontWeight = FontWeight.Normal,
        color = ColorProvider(Color.White),
        fontSize = 14.sp
      )
    )
    Text(
      text = discomfortString,
      style = TextStyle(
        fontWeight = FontWeight.Normal,
        color = ColorProvider(Color.White),
        fontSize = 14.sp
      )
    )
  }
}
