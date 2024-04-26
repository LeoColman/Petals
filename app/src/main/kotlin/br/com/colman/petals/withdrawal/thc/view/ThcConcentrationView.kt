/*
 * Petals APP
 * Copyright (C) 2021 Leonardo Colman Lopes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package br.com.colman.petals.withdrawal.thc.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import br.com.colman.petals.R.string.current_thc_concentration
import br.com.colman.petals.R.string.days
import br.com.colman.petals.R.string.thc_concentration
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.interpolator.ThcConcentrationDataPoints
import br.com.colman.petals.withdrawal.thc.repository.ThcConcentrationRepository
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit

@Suppress("FunctionName")
class ThcConcentrationView(
  private val useRepository: UseRepository,
  private val repository: ThcConcentrationRepository,
) {

  @SuppressLint("FlowOperatorInvokedInComposition")
  @Composable
  fun Content() {
    val quitDate by useRepository.getLastUseDate().filterNotNull().collectAsState(now())
    val currentPercentage by repository.concentration.map { it * 100 }.collectAsState(100.0)
    val quitDays = ChronoUnit.SECONDS.between(quitDate, now()).toDouble().div(86400)

    val graphTitle = stringResource(current_thc_concentration, "%.3f".format(currentPercentage))
    val colors = MaterialTheme.colors

    AndroidView({ createGraph(it, currentPercentage, quitDays, colors) }, update = {
      it.title = graphTitle
      it.removeAllSeries()
      it.addSeries(concentrationSeries())
      it.addSeries(currentPercentagePoint(currentPercentage, quitDays))
      it.invalidate()
    })
  }

  private fun createGraph(context: Context, currentPercentage: Double, quitDay: Double, colors: Colors) =
    GraphView(context).apply {
      addSeries(concentrationSeries())
      addSeries(currentPercentagePoint(currentPercentage, quitDay))

      viewport.apply {
        isYAxisBoundsManual = true
        setMaxY(100.0)
        setMinY(0.0)
        isXAxisBoundsManual = true
        setMaxX(15.0)
        setMinX(0.0)
      }

      gridLabelRenderer.apply {
        titleColor = colors.primary.toArgb()
        verticalAxisTitleColor = colors.primary.toArgb()
        horizontalAxisTitleColor = colors.primary.toArgb()
        horizontalLabelsColor = colors.primary.toArgb()
        verticalLabelsColor = colors.primary.toArgb()

        verticalAxisTitle = context.getString(thc_concentration)
        horizontalAxisTitle = context.getString(days)
      }
    }

  private fun concentrationSeries(): LineGraphSeries<DataPoint> {
    val dataPoints = abstinenceThcPercentages().map {
      DataPoint(it.key.toDays().toDouble(), it.value)
    }

    return LineGraphSeries(dataPoints.toTypedArray()).apply {
      isDrawDataPoints = true
    }
  }

  private fun abstinenceThcPercentages(): Map<Duration, Double> {
    val maxValue = ThcConcentrationDataPoints.values.max()
    return ThcConcentrationDataPoints.mapValues { it.value / maxValue * 100 }
  }

  private fun currentPercentagePoint(percentage: Double, day: Double) =
    PointsGraphSeries(arrayOf(DataPoint(day, percentage))).apply {
      size = 24f
      color = Color.parseColor("#059033")
      shape = PointsGraphSeries.Shape.POINT
    }
}
