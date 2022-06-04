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

package br.com.colman.petals.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R.string.*
import br.com.colman.petals.use.io.UseCsvFileImporter
import br.com.colman.petals.use.io.UseExporter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun MyTopAppBar() {
  TopAppBar {
    Box(
      Modifier
        .padding(16.dp)
        .height(56.dp)
        .fillMaxWidth()
    ) {
      MyTopAppBarContent()
    }
  }
}

@Composable
fun MyTopAppBarContent() {
  val coroutineScope = rememberCoroutineScope()

  Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
    Text(stringResource(app_name), fontWeight = FontWeight.Bold, fontSize = 20.sp)
    Box(
      Modifier.clickable {
        coroutineScope.launch {
        }
      }
    ) {
      Row(Modifier, spacedBy(16.dp)) {
        ImportButton()
        ExportButton(get())
      }
    }
  }
}

@Preview
@Composable
private fun ImportButton(
  useCsvFileImporter: UseCsvFileImporter = get()
) {
  val launcher = rememberLauncherForActivityResult(GetContent()) {
    if (it != null) {
      useCsvFileImporter.importCsvFile(it)
    }
  }

  Box(Modifier.clickable { launcher.launch("text/*") }) {
    Text(stringResource(import_import), fontSize = 14.sp)
  }
}

@Composable
private fun ExportButton(
  useExporter: UseExporter
) {
  val coroutineScope = rememberCoroutineScope()
  val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

  Box(
    Modifier.clickable {
      coroutineScope.launch {
        useExporter.exportUses(launcher)
      }
    }
  ) {
    Text(stringResource(export_export), fontSize = 14.sp)
  }
}
