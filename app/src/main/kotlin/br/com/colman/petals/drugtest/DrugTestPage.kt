/*
 * Petals APP
 * Copyright (C) 2026 Leonardo Colman Lopes
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

package br.com.colman.petals.drugtest

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R
import br.com.colman.petals.R.string.blood_test_ready
import br.com.colman.petals.R.string.blood_test_source
import br.com.colman.petals.R.string.drug_test_intro
import br.com.colman.petals.R.string.hair_test_ready
import br.com.colman.petals.R.string.hair_test_source
import br.com.colman.petals.R.string.saliva_test_ready
import br.com.colman.petals.R.string.saliva_test_source
import br.com.colman.petals.R.string.until_blood_test
import br.com.colman.petals.R.string.until_hair_test
import br.com.colman.petals.R.string.until_saliva_test
import br.com.colman.petals.R.string.until_urine_test
import br.com.colman.petals.R.string.urine_test_ready
import br.com.colman.petals.R.string.urine_test_source
import br.com.colman.petals.use.repository.UseRepository
import org.koin.compose.koinInject
import java.time.LocalDate

/**
 * Andås HT, Krabseth HM, Enger A, Marcussen BN, Haneborg AM, Christophersen AS,
 * Vindenes V, Øiestad EL. Detection time for THC in oral fluid after frequent cannabis smoking.
 * Ther Drug Monit. 2014 Dec;36(6):808-14. doi: 10.1097/FTD.0000000000000092. PMID: 24819969.
 */
private const val SalivaTestDays = 8

/**
 * Goodwin, R. S., W. D. Darwin, C. N. Chiang, M. Shih, S.-H. Li, and M. A. Huestis.
 * “Urinary Elimination of 11-Nor-9-Carboxy- 9-Tetrahydrocannnabinol in Cannabis Users During Continuously Monitored
 * Abstinence.” Journal of Analytical Toxicology 32, no. 8 (2008): 562–69. https://doi.org/10.1093/jat/32.8.562.
 */
private const val UrineTestDays = 28

/**
 * Taylor M, Lees R, Henderson G, Lingford-Hughes A, Macleod J, Sullivan J, Hickman M.
 * Comparison of cannabinoids in hair with self-reported cannabis consumption in heavy,
 * light and non-cannabis users. Drug Alcohol Rev. 2017 Mar;36(2):220-226. doi: 10.1111/dar.12412.
 * Epub 2016 Jun 14. PMID: 27296783; PMCID: PMC5396143.
 */
private const val HairTestDays = 90

/**
 * Bergamaschi MM, Karschner EL, Goodwin RS, Scheidweiler KB, Hirvonen J, Queiroz RH, Huestis MA.
 * Impact of prolonged cannabinoid excretion in chronic daily cannabis smokers' blood on per se drugged driving laws.
 * Clin Chem. 2013 Mar;59(3):519-26. doi: 10.1373/clinchem.2012.195503. PMID: 23449702; PMCID: PMC3717350.
 */
private const val BloodTestDays = 30

@Composable
fun DrugTestPage(useRepository: UseRepository = koinInject()) {
  val lastUseDate by useRepository.getLastUseDate().collectAsState(initial = null)

  Box(Modifier.fillMaxSize().padding(16.dp), Alignment.Center) {
    Column(Modifier.verticalScroll(rememberScrollState()), Arrangement.Center, Alignment.CenterHorizontally) {
      Text(stringResource(drug_test_intro), style = typography.body1, textAlign = TextAlign.Center)

      Spacer(Modifier.size(24.dp))

      SalivaTestContent(lastUseDate?.toLocalDate())

      Spacer(Modifier.size(16.dp))

      UrineTestContent(lastUseDate?.toLocalDate())

      Spacer(Modifier.size(16.dp))

      HairTestContent(lastUseDate?.toLocalDate())

      Spacer(Modifier.size(16.dp))

      BloodTestContent(lastUseDate?.toLocalDate())
    }
  }
}

@Composable
fun SalivaTestContent(lastUseDate: LocalDate?) {
  DrugTestSection(lastUseDate, SalivaTestDays, saliva_test_ready, until_saliva_test, saliva_test_source)
}

@Composable
fun UrineTestContent(lastUseDate: LocalDate?) {
  DrugTestSection(lastUseDate, UrineTestDays, urine_test_ready, until_urine_test, urine_test_source)
}

@Composable
fun HairTestContent(lastUseDate: LocalDate?) {
  DrugTestSection(lastUseDate, HairTestDays, hair_test_ready, until_hair_test, hair_test_source)
}

@Composable
fun BloodTestContent(lastUseDate: LocalDate?) {
  DrugTestSection(lastUseDate, BloodTestDays, blood_test_ready, until_blood_test, blood_test_source)
}

@Composable
private fun DrugTestSection(
  lastUseDate: LocalDate?,
  limitDays: Int,
  @StringRes readyText: Int,
  @StringRes untilText: Int,
  @StringRes sourceText: Int,
) {
  val daysSince = computeDaysSince(lastUseDate)
  val daysRemaining = computeDaysRemaining(daysSince, limitDays)

  Box(contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
      ProgressBarWithEndpoints(daysSince, limitDays)

      when {
        lastUseDate == null -> {
          Text(text = stringResource(R.string.no_previous_use_found))
        }
        daysRemaining == 0 -> {
          Text(text = stringResource(readyText))
        }
        else -> {
          val remaining = daysRemaining ?: 0
          Text(text = pluralStringResource(R.plurals.amount_days, remaining, remaining))
          Text(text = stringResource(untilText))
        }
      }

      Spacer(Modifier.size(8.dp))
      Text(
        text = stringResource(sourceText),
        style = typography.caption,
        fontSize = 8.sp,
        textAlign = TextAlign.Center
      )
    }
  }
}

private fun computeDaysSince(lastUseDate: LocalDate?): Int? {
  return lastUseDate?.let { last ->
    val todayEpoch = LocalDate.now().toEpochDay()
    val lastEpoch = last.toEpochDay()
    val since = (todayEpoch - lastEpoch).toInt()
    since.coerceAtLeast(0)
  }
}

private fun computeDaysRemaining(daysSince: Int?, limitDays: Int): Int? {
  return daysSince?.let { (limitDays - it).coerceAtLeast(0) }
}

@Composable
private fun ProgressBarWithEndpoints(daysSince: Int?, limitDays: Int) {
  if (daysSince != null) {
    val progress = (minOf(daysSince, limitDays) / limitDays.toFloat())
    LinearProgressIndicator(
      progress = progress,
      modifier = Modifier
        .fillMaxWidth()
        .height(10.dp)
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = "0")
      Text(text = limitDays.toString())
    }
  }
}
