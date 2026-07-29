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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.plurals.amount_days
import br.com.colman.petals.R.string.effective_abstinence_disabled
import br.com.colman.petals.R.string.effective_abstinence_explained
import br.com.colman.petals.R.string.effective_abstinence_sessions_mode
import br.com.colman.petals.R.string.effective_abstinence_unavailable
import br.com.colman.petals.R.string.symptoms_introduction
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.data.ChartConfig
import br.com.colman.petals.withdrawal.tolerance.AbstinenceEstimate
import br.com.colman.petals.withdrawal.tolerance.EstimateMode.Disabled
import br.com.colman.petals.withdrawal.tolerance.EstimateMode.Grams
import br.com.colman.petals.withdrawal.tolerance.EstimateMode.LastUseOnly
import br.com.colman.petals.withdrawal.tolerance.EstimateMode.Sessions
import br.com.colman.petals.withdrawal.tolerance.ToleranceLookbackDays
import br.com.colman.petals.withdrawal.tolerance.estimateAbstinence
import br.com.colman.petals.withdrawal.view.SecondsPerDay
import br.com.colman.petals.withdrawal.view.WithdrawalChart
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes

@Composable
fun SymptomsPage(
  useRepository: UseRepository = koinInject(),
  settingsRepository: SettingsRepository = koinInject()
) {
  // Pinned to a day boundary so the query, and therefore the subscription, stays stable while the tab is open.
  val windowStart = remember { LocalDate.now().minusDays(ToleranceLookbackDays).atStartOfDay() }
  val usesFlow = remember(windowStart) { useRepository.since(windowStart) }

  val uses by usesFlow.collectAsState(emptyList())
  val lastUseDate by useRepository.getLastUseDate().collectAsState(null)
  val isToleranceModelEnabled by settingsRepository.isToleranceModelEnabled.collectAsState(true)

  var now by remember { mutableStateOf(LocalDateTime.now()) }
  LaunchedEffect(Unit) {
    while (true) {
      delay(1.minutes)
      now = LocalDateTime.now()
    }
  }

  val estimate = remember(uses, lastUseDate, isToleranceModelEnabled, now) {
    estimateAbstinence(uses, lastUseDate, now, isToleranceModelEnabled)
  }

  SymptomsOverview(estimate)
}

@Composable
@Preview
private fun SymptomsPreview() {
  SymptomsOverview(AbstinenceEstimate(Duration.ofDays(10), Duration.ofHours(2), Grams))
}

@Composable
@Preview
private fun SymptomsNullPreview() {
  SymptomsOverview(null)
}

@Composable
private fun SymptomsOverview(estimate: AbstinenceEstimate?) {
  Column(Modifier.verticalScroll(rememberScrollState()).padding(8.dp, 8.dp, 8.dp, 64.dp), spacedBy(16.dp)) {
    Text(stringResource(symptoms_introduction))
    AbstinenceExplanation(estimate)
    ChartList(estimate?.effective)
  }
}

/**
 * Both numbers, always. A relapse can leave the effective reading far above the literal time since the
 * last use, and the Usage tab keeps showing the literal one, so hiding it would read as a contradiction.
 */
@Composable
private fun AbstinenceExplanation(estimate: AbstinenceEstimate?) {
  if (estimate == null) return

  Column(verticalArrangement = spacedBy(4.dp)) {
    val fallbackReason = when (estimate.mode) {
      LastUseOnly -> effective_abstinence_unavailable
      Disabled -> effective_abstinence_disabled
      else -> null
    }
    if (fallbackReason != null) {
      Text(stringResource(fallbackReason), style = MaterialTheme.typography.caption)
      return@Column
    }

    Text(
      stringResource(effective_abstinence_explained, estimate.effective.asDays(), estimate.actual.asDays()),
      style = MaterialTheme.typography.caption
    )
    if (estimate.mode == Sessions) {
      Text(stringResource(effective_abstinence_sessions_mode), style = MaterialTheme.typography.caption)
    }
  }
}

@Composable
private fun Duration.asDays(): String {
  val days = seconds.toDouble() / SecondsPerDay
  val rounded = (days * 10).roundToInt() / 10.0
  val quantity = if (rounded == 1.0) 1 else 2
  return pluralStringResource(amount_days, quantity, "%.1f".format(rounded))
}

@Composable
private fun ChartList(effectiveAbstinence: Duration?) {
  ChartConfig.entries().forEach { chartConfig ->
    WithdrawalChartBox(chartConfig, effectiveAbstinence)
  }
}

@Composable
private fun WithdrawalChartBox(chartConfig: ChartConfig, effectiveAbstinence: Duration?) {
  Box(Modifier.height(250.dp)) {
    WithdrawalChart(
      effectiveAbstinence = effectiveAbstinence,
      data = chartConfig.data,
      graphTitle = { getString(chartConfig.title, "%.2f".format(it ?: 0.0)) },
      verticalAxisTitle = stringResource(chartConfig.verticalAxisTitle),
      horizontalAxisTitle = stringResource(chartConfig.horizontalAxisTitle),
      maxX = chartConfig.maxX,
      maxY = chartConfig.maxY
    )
  }
}
