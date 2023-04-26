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

package br.com.colman.petals.withdrawal.discomfort.view

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
import br.com.colman.petals.R.string.current_withdrawal_discomfort
import br.com.colman.petals.R.string.days
import br.com.colman.petals.R.string.discomfort_strength
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.discomfort.repository.DiscomfortRepository
import br.com.colman.petals.withdrawal.interpolator.DiscomfortDataPoints
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit

@Suppress("FunctionName")
class DiscomfortView(
  private val useRepository: UseRepository,
  private val discomfortRepository: DiscomfortRepository,
) {

  @SuppressLint("FlowOperatorInvokedInComposition")
  @Composable
  fun Content() {
    val quitDate by useRepository.getLastUseDate().filterNotNull().collectAsState(now())
    val currentPercentage by discomfortRepository.discomfort.map { it.strength }.collectAsState(8.0)
    val quitDays = ChronoUnit.SECONDS.between(quitDate, now()).toDouble().div(86400)
    val colors = MaterialTheme.colors

    val graphTitle = stringResource(current_withdrawal_discomfort, "%.2f".format(currentPercentage))
    AndroidView({ createGraph(it, currentPercentage, quitDays, colors) }, update = {
      it.title = graphTitle
      it.removeAllSeries()
      it.addSeries(discomfortSeries())
      it.addSeries(currentDiscomfortPoint(currentPercentage, quitDays))
      it.invalidate()
    })
  }

  private fun createGraph(context: Context, currentPercentage: Double, quitDay: Double, colors: Colors) =
    GraphView(context).apply {
      addSeries(discomfortSeries())
      addSeries(currentDiscomfortPoint(currentPercentage, quitDay))

      viewport.apply {
        isYAxisBoundsManual = true
        setMaxY(10.0)
        setMinY(0.0)
        isXAxisBoundsManual = true
        setMaxX(27.0)
        setMinX(0.0)
      }

      gridLabelRenderer.apply {
        titleColor = colors.primary.toArgb()
        verticalAxisTitleColor = colors.primary.toArgb()
        horizontalAxisTitleColor = colors.primary.toArgb()
        horizontalLabelsColor = colors.primary.toArgb()
        verticalLabelsColor = colors.primary.toArgb()

        verticalAxisTitle = context.getString(discomfort_strength)
        horizontalAxisTitle = context.getString(days)
      }
    }

  private fun discomfortSeries(): LineGraphSeries<DataPoint> {
    val dataPoints = DiscomfortDataPoints.map { (key, value) ->
      DataPoint(key.toDays().toDouble(), value.strength)
    }

    return LineGraphSeries(dataPoints.toTypedArray()).apply {
      isDrawDataPoints = true
      dataPointsRadius = 8f
    }
  }

  private fun currentDiscomfortPoint(percentage: Double, day: Double) =
    PointsGraphSeries(arrayOf(DataPoint(day, percentage))).apply {
      size = 20f
      color = Color.parseColor("#059033")
      shape = PointsGraphSeries.Shape.POINT
    }
}
