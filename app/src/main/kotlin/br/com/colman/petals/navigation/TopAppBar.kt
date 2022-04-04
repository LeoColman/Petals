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

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import br.com.colman.petals.BuildConfig.APPLICATION_ID
import br.com.colman.petals.R
import br.com.colman.petals.R.string.*
import br.com.colman.petals.use.repository.UseExporter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import timber.log.Timber
import java.io.File

@Composable
fun MyTopAppBar() {
  TopAppBar {
    Box(
      Modifier
        .padding(16.dp)
        .height(56.dp)
        .fillMaxWidth()
    ) {
      MyTopAppBarContent(get())
    }
  }
}

@Composable
fun MyTopAppBarContent(
  useExporter: UseExporter
) {
  val coroutineScope = rememberCoroutineScope()
  val context = get<Context>()
  val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

  val dateLabel = stringResource(date_label)
  val amountLabel = stringResource(amount_label)
  val costPerGramLabel = stringResource(cost_per_gram_label)

  Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

    Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold, fontSize = 20.sp)
    Box(
      Modifier.clickable {
        coroutineScope.launch {
          exportUses(useExporter.toCsvFileContent(dateLabel, amountLabel, costPerGramLabel), context, launcher)
        }
      }
    ) {
      Text(stringResource(export), fontSize = 14.sp)
    }
  }
}

private fun exportUses(
  usesCsv: String,
  context: Context,
  otherContet: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
  val contentUri = createCsvFile(usesCsv, context)

  val intent = Intent().apply {
    data = contentUri
    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_STREAM, contentUri)
  }

  otherContet.launch(intent)
}

private fun createCsvFile(usesCsv: String, context: Context): Uri {
  Timber.d("Creating CSV intent $usesCsv")
  val exports = File(context.filesDir, "exports")
  if (!exports.exists()) {
    exports.mkdirs()
  }
  val export = File(exports, "export.csv")
  export.createNewFile()
  export.writeText(usesCsv)

  return FileProvider.getUriForFile(context, APPLICATION_ID, export, "export.csv")
}
