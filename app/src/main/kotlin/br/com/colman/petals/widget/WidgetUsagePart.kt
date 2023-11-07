package br.com.colman.petals.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import br.com.colman.petals.R
import br.com.colman.petals.use.TimeUnit
import br.com.colman.petals.utils.truncatedToMinute
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun WidgetUsagePart(
  lastUseDate: LocalDateTime?,
  dateString: String,
  labels: List<Pair<TimeUnit, Long>>
) {
  Column(
    modifier = GlanceModifier.padding(4.dp),
    verticalAlignment = Alignment.Vertical.CenterVertically,
    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
  ) {
    Column(
      verticalAlignment = Alignment.CenterVertically,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = LocalContext.current.getString(R.string.quit_date_text),
        style = TextStyle(
          fontWeight = FontWeight.Medium,
          color = ColorProvider(Color.White),
          fontSize = 20.sp
        )
      )
      val dateStringWithExtras = if (!lastUseDate!!.is420()) dateString else "$dateString 🥦🥦"
      Text(
        text = dateStringWithExtras,
        style = TextStyle(
          fontWeight = FontWeight.Medium,
          color = ColorProvider(Color.White),
          fontSize = 20.sp
        )
      )
    }

    Column(
      verticalAlignment = Alignment.CenterVertically,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Years, Months, and Days
      Column(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Years, Months, and Days
        Row(
          modifier = GlanceModifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          labels.filter { it.first in listOf(TimeUnit.Year, TimeUnit.Month, TimeUnit.Day) }
            .forEach { (label, amount) ->
              Row() {
                Text(
                  text = LocalContext.current.getString(label.unitName), style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    color = ColorProvider(Color.White),
                    fontSize = 16.sp
                  )
                )
                Text(
                  text = ": $amount ", style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    color = ColorProvider(Color.White),
                    fontSize = 16.sp
                  )
                )
              }
            }
        }
        // Hours, Minutes, and Seconds
        Row(
          modifier = GlanceModifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          labels.filter { it.first in listOf(TimeUnit.Hour, TimeUnit.Minute, TimeUnit.Second) }
            .forEach { (label, amount) ->
              Row() {
                if (!LocalContext.current.getString(label.unitName).equals("Hours")) {
                  Text(
                    text = formatLongAsTwoDigitString(amount), style = TextStyle(
                      fontWeight = FontWeight.Normal,
                      color = ColorProvider(Color.White),
                      fontSize = 16.sp
                    )
                  )
                } else {
                  Text(
                    text = "$amount", style = TextStyle(
                      fontWeight = FontWeight.Normal,
                      color = ColorProvider(Color.White),
                      fontSize = 16.sp
                    )
                  )
                }
                if (!LocalContext.current.getString(label.unitName).equals("Seconds")) {
                  Text(
                    text = ":", style = TextStyle(
                      fontWeight = FontWeight.Normal,
                      color = ColorProvider(Color.White),
                      fontSize = 16.sp
                    )
                  )
                }
              }
            }
        }
      }
    }
  }
}

private fun LocalDateTime.is420() = toLocalTime().truncatedToMinute() == LocalTime.of(16, 20)

private fun formatLongAsTwoDigitString(input: Long): String {
  return String.format("%02d", input)
}

