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
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.colman.petals.BuildConfig
import br.com.colman.petals.R.string.app_name
import br.com.colman.petals.R.string.export_export
import br.com.colman.petals.R.string.import_import
import br.com.colman.petals.R.string.settings
import br.com.colman.petals.use.io.UseCsvFileImporter
import br.com.colman.petals.use.io.UseExporter
import compose.icons.TablerIcons
import compose.icons.tablericons.InfoCircle
import compose.icons.tablericons.Settings
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun MyTopAppBar(navController: NavController) {
  TopAppBar {
    Box(
      Modifier
        .padding(16.dp)
        .height(56.dp)
        .fillMaxWidth()
    ) {
      MyTopAppBarContent(navController)
    }
  }
}


@Composable
fun MyTopAppBarContent(navController: NavController) {
  Row(Modifier.fillMaxWidth(), SpaceBetween, CenterVertically) {
    AppAndVersionName()
    Row(Modifier, spacedBy(16.dp)) {
        ImportExportButtons()
        InfoSettingsButton(navController)
    }
  }
}

@Composable
fun AppAndVersionName() {
  Row {
    Text(
      modifier = Modifier.alignByBaseline(),
      text = stringResource(app_name),
      fontWeight = FontWeight.Bold,
      fontSize = 20.sp
    )
    Text(
      modifier = Modifier.alignByBaseline(),
      text = " v${BuildConfig.VERSION_NAME}",
      fontWeight = FontWeight.Light,
      fontSize = 10.sp
    )
  }
}

@Composable
 fun InfoSettingsButton(navController: NavController) {
  InfoButton(navController)
  SettingsButton(navController)
}

@Composable
 fun ImportExportButtons() {
  ImportButton()
  ExportButton(koinInject())
}

@Preview
@Composable
private fun ImportButton(
  useCsvFileImporter: UseCsvFileImporter = koinInject()
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

@Composable
private fun SettingsButton(
  navController: NavController
) {
  Icon(
    TablerIcons.Settings,
    stringResource(settings),
    Modifier.clickable {
      navController.navigate(Screens.Settings)
    }
  )
}

@Composable
private fun InfoButton(
  navController: NavController
) {
  Icon(
    TablerIcons.InfoCircle,
    stringResource(settings),
    Modifier.clickable {
      navController.navigate(Screens.Information)
    }.testTag("InfoButton")
  )
}
