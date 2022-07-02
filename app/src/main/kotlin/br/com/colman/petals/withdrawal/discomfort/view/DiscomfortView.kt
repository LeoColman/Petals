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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import br.com.colman.petals.R.string.*
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.discomfort.repository.DiscomfortRepository
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
  private val repository: DiscomfortRepository,
) {

  @SuppressLint("FlowOperatorInvokedInComposition")
  @Composable
  fun Content() {
    val quitDate by useRepository.getLastUseDate().filterNotNull().collectAsState(now())
    val currentPercentage by repository.discomfort.map { it.discomfortStrength }.collectAsState(8.0)
    val quitDays = ChronoUnit.SECONDS.between(quitDate, now()).toDouble().div(86400)

    val graphTitle = stringResource(current_withdrawal_discomfort, "%.2f".format(currentPercentage))
    AndroidView({ createGraph(it, currentPercentage, quitDays) }, update = {
      it.title = graphTitle
      it.removeAllSeries()
      it.addSeries(discomfortSeries())
      it.addSeries(currentDiscomfortPoint(currentPercentage, quitDays))
      it.invalidate()
    })
  }

  private fun createGraph(context: Context, currentPercentage: Double, quitDay: Double) = GraphView(context).apply {
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
      verticalAxisTitle = context.getString(discomfort_strength)
      horizontalAxisTitle = context.getString(days)
    }
  }

  private fun discomfortSeries(): LineGraphSeries<DataPoint> {
    val dataPoints = repository.discomfortDays.map {
      DataPoint(it.key.days.toDouble(), it.value)
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
