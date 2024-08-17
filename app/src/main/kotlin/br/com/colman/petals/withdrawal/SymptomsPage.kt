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

package br.com.colman.petals.withdrawal

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.symptoms_introduction
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.data.ChartConfig
import br.com.colman.petals.withdrawal.view.WithdrawalChart
import org.koin.compose.koinInject

@Composable
@Preview
fun Symptoms(
  useRepository: UseRepository = koinInject()
) {
  val scrollState = rememberScrollState()
  val lastUseDate by useRepository.getLastUseDate().collectAsState(null)

  Column(Modifier.verticalScroll(scrollState).padding(8.dp, 8.dp, 8.dp, 64.dp), spacedBy(16.dp)) {
    Text(stringResource(symptoms_introduction))

    ChartConfig.entries().forEach { chart ->
      Box(Modifier.height(250.dp)) {
        WithdrawalChart(
          lastUseDate = lastUseDate,
          data = chart.data,
          graphTitle = { getString(chart.title, "%.2f".format(it ?: 0.0)) },
          verticalAxisTitle = stringResource(chart.verticalAxisTitle),
          horizontalAxisTitle = stringResource(chart.horizontalAxisTitle),
          maxX = chart.maxX,
          maxY = chart.maxY
        )
      }
    }
  }
}
