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

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.AndroidView
import br.com.colman.petals.clock.QuitTimer
import br.com.colman.petals.withdrawal.thc.repository.ThcConcentrationRepository
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.joda.time.Days
import org.joda.time.LocalDateTime.now
import org.joda.time.Seconds.secondsBetween


@Suppress("FunctionName")
class ThcConcentrationView(
  private val quitTimer: QuitTimer,
  private val repository: ThcConcentrationRepository,
) {

  @Composable
  fun Content() {
    val quitDate by quitTimer.quitDate.filterNotNull().collectAsState(now())
    val currentPercentage by repository.concentration.map { it.percentageOnBodyFromStart }.collectAsState(100.0)
    val quitDays = secondsBetween(quitDate, now()).seconds.toDouble().div(86400)

    AndroidView({ createGraph(it, currentPercentage, quitDays) }, update = {
      it.title = "Current  THC Concentration: " + String.format("%.3f", currentPercentage) + "%"
      it.removeAllSeries()
      it.addSeries(concentrationSeries())
      it.addSeries(currentPercentagePoint(currentPercentage, quitDays))
      it.invalidate()
    })
  }

  private fun createGraph(context: Context, currentPercentage: Double, quitDay: Double) = GraphView(context).apply {
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
      verticalAxisTitle = "THC Concentration(%)"
      horizontalAxisTitle = "Days"
    }
  }

  private fun concentrationSeries(): LineGraphSeries<DataPoint> {
    val dataPoints = abstinenceThcPercentages().map {
      DataPoint(it.key.days.toDouble(), it.value)
    }

    return LineGraphSeries(dataPoints.toTypedArray())
  }

  private fun abstinenceThcPercentages(): Map<Days, Double> {
    val maxValue = repository.abstinenceThc.values.maxOrNull()!!
    return repository.abstinenceThc.mapValues { it.value / maxValue * 100 }
  }

  private fun currentPercentagePoint(percentage: Double, day: Double) =
    PointsGraphSeries(arrayOf(DataPoint(day, percentage))).apply {
      size = 24f
      color = Color.parseColor("#059033")
      shape =  PointsGraphSeries.Shape.POINT
    }
}