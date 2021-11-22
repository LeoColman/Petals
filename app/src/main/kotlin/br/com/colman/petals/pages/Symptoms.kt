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

package br.com.colman.petals.pages

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R
import br.com.colman.petals.withdrawal.discomfort.view.DiscomfortView
import br.com.colman.petals.withdrawal.thc.view.ThcConcentrationView
import org.koin.androidx.compose.get

@Composable
@Preview
fun Symptoms(
  thcConcentrationView: ThcConcentrationView = get(),
  discomfortView: DiscomfortView = get()
) {
  val scrollState = rememberScrollState()

  Column(Modifier.verticalScroll(scrollState).padding(8.dp, 8.dp, 8.dp, 64.dp), spacedBy(16.dp)) {
    Text(stringResource(R.string.symptoms_introduction))
    Box(Modifier.height(250.dp)) {
      thcConcentrationView.Content()
    }

    Box(Modifier.height(250.dp)) {
      discomfortView.Content()
    }
  }
}