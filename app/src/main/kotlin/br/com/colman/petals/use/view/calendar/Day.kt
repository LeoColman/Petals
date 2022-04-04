package br.com.colman.petals.use.view.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.use.repository.*
import br.com.colman.petals.use.view.prensadoUse
import java.time.LocalDate
import java.time.LocalDate.now

@Preview
@Composable
fun UseDayPreview() {
  UseDay(now(), listOf(now()), listOf(prensadoUse(now().atStartOfDay())))
}

@Preview
@Composable
fun HalfUseDayPreview() {
  UseDay(now(), listOf(now()), List(5) { prensadoUse(now().atStartOfDay()) })
}

@Preview
@Composable
fun FullUseDayPreview() {
  UseDay(now(), listOf(now()), List(10) { prensadoUse(now().atStartOfDay()) })
}

@Composable
fun UseDay(
  date: LocalDate,
  selectedDays: List<LocalDate>,
  uses: List<Use>
) {
  val currentDayColor = colors.primary
  val selectionColor = colors.secondary

  val isCurrentDay = date == now()
  val isFromCurrentMonth = date.month == now().month
  val isSelected = date in selectedDays

  val selectedUses = uses.filter { it.localDate in selectedDays }
  val dayUses = uses.filter { it.localDate == date }

  Card(
    Modifier.aspectRatio(1f).padding(2.dp),
    elevation = if (isFromCurrentMonth) 4.dp else 0.dp,
    border = if (isCurrentDay) BorderStroke(1.dp, currentDayColor) else null,
    contentColor = if (isSelected) selectionColor else contentColorFor(colors.surface)
  ) {

    Column(Modifier.fillMaxSize(), SpaceBetween, CenterHorizontally) {
      Text(date.dayOfMonth.toString(), Modifier.align(CenterHorizontally))

      Column(Modifier.padding(8.dp), Arrangement.spacedBy(4.dp)) {

        fun magicFunction(x: Double, range: ClosedFloatingPointRange<Double>, a: Float, b: Float): Double {
          if (x == 0.0) return 0.0
          val min = range.start
          val max = range.endInclusive

          return ((b - a) * (x - min) / (max - min)) + a
        }

        val dayGrams = dayUses.totalGrams.toDouble()

        val minGramsInPeriod = selectedUses.minGrams
        val maxGramsInPeriod = selectedUses.maxGrams
        val averageGramsInPeriod = selectedUses.averageGrams

        val firstGramsRange = minGramsInPeriod..averageGramsInPeriod
        val secondGRamsRange = averageGramsInPeriod..maxGramsInPeriod

        val firstSizeRange = 0.1f..1f
        val secondSizeRange = 1f..1.5f

        val gramsRangeIShouldUse = if (dayGrams <= averageGramsInPeriod) {
          firstGramsRange
        } else {
          secondGRamsRange
        }

        val sizeRangeIShouldUse = if (dayGrams <= averageGramsInPeriod) {
          firstSizeRange
        } else {
          secondSizeRange
        }

        val magicValue = magicFunction(dayGrams, gramsRangeIShouldUse, sizeRangeIShouldUse.start, sizeRangeIShouldUse.endInclusive)

        GreenCircle(Modifier.scale(magicValue.toFloat()))
      }
    }
  }
}

@Composable
fun GreenCircle(modifier: Modifier) {
  Box(modifier.aspectRatio(1f, true).clip(CircleShape).background(Green))
}
