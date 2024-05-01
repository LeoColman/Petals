package br.com.colman.petals.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.layout.Alignment.Horizontal.Companion.CenterHorizontally
import androidx.glance.layout.Alignment.Vertical.Companion.CenterVertically
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import br.com.colman.petals.R
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.data.DiscomfortDataPoints
import br.com.colman.petals.withdrawal.data.ThcConcentrationDataPoints
import br.com.colman.petals.withdrawal.interpolator.Interpolator
import br.com.colman.petals.withdrawal.view.SecondsPerDay
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.filterNotNull
import org.koin.compose.koinInject

@Composable
fun WidgetConcentrationDiscomfortPart() {
  val useRepository = koinInject<UseRepository>()

  val lastUseDate by useRepository.getLastUseDate().filterNotNull().collectAsState(LocalDateTime.now().minusYears(10))
  val thcInterpolator = Interpolator(ThcConcentrationDataPoints)
  val discomfortInterpolator = Interpolator(DiscomfortDataPoints)

  val currentPercentageTHC = thcInterpolator.calculatePercentage(ChronoUnit.SECONDS.between(lastUseDate, LocalDateTime.now())) * 100

  val currentDiscomfort = discomfortInterpolator.value(ChronoUnit.SECONDS.between(lastUseDate, LocalDateTime.now()).toDouble().div(
    SecondsPerDay))

  val thcConcentrationString = LocalContext.current.getString(
    R.string.current_thc_concentration,
    "%.3f".format(currentPercentageTHC)
  )

  val discomfortString = LocalContext.current.getString(
    R.string.current_withdrawal_discomfort,
    "%.3f".format(currentDiscomfort)
  )

  Column(
    GlanceModifier.padding(4.dp),
    CenterVertically,
    CenterHorizontally
  ) {
    Text(
      thcConcentrationString,
      style = TextStyle(
        fontWeight = FontWeight.Normal,
        color = ColorProvider(Color.White),
        fontSize = 14.sp
      )
    )
    Text(
      discomfortString,
      style = TextStyle(
        fontWeight = FontWeight.Normal,
        color = ColorProvider(Color.White),
        fontSize = 14.sp
      )
    )
  }
}
