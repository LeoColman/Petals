package br.com.colman.petals.settings.view.listitem

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocumentTree
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.auto_export
import br.com.colman.petals.R.string.auto_export_chosen_folder
import br.com.colman.petals.R.string.auto_export_description
import br.com.colman.petals.R.string.auto_export_folder_unavailable
import br.com.colman.petals.R.string.auto_export_last_export
import br.com.colman.petals.R.string.auto_export_last_failed
import br.com.colman.petals.R.string.auto_export_never_exported
import br.com.colman.petals.R.string.auto_export_secondary
import br.com.colman.petals.settings.view.dialog.SwitchListItem
import compose.icons.TablerIcons
import compose.icons.tablericons.Folder
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Preview
@Composable
fun AutoExportListItem(
  isEnabled: Boolean,
  status: AutoExportStatus,
  onFolderPicked: suspend (Uri) -> Unit = {},
  onDisable: suspend () -> Unit = {},
) {
  // Both callbacks read DataStore and query the document provider, which is disk IO.
  // Launching them keeps that off the main thread; the enabler hops to IO itself.
  val scope = rememberCoroutineScope()

  val launcher = rememberLauncherForActivityResult(OpenDocumentTree()) { treeUri ->
    if (treeUri != null) scope.launch { onFolderPicked(treeUri) }
  }

  SwitchListItem(
    icon = TablerIcons.Folder,
    text = stringResource(auto_export),
    description = secondaryText(status),
    // Controlled by the DataStore flow (isEnabled), not local mutableStateOf:
    // if the user backs out of the picker, nothing is persisted, the flow
    // never emits, and the switch snaps back off on its own.
    initialState = isEnabled,
    onChangeState = { checked -> if (checked) launcher.launch(null) else scope.launch { onDisable() } }
  )
}

@Composable
private fun secondaryText(status: AutoExportStatus): String {
  val folderName = status.folderName ?: return stringResource(auto_export_description)

  val statusText = when {
    status.lastError == "permission" -> stringResource(auto_export_folder_unavailable)
    status.lastError == "io" -> stringResource(auto_export_last_failed)
    status.lastSuccessAtEpochMillis != null ->
      stringResource(auto_export_last_export, formatTimestamp(status.lastSuccessAtEpochMillis))
    else -> stringResource(auto_export_never_exported)
  }

  return stringResource(auto_export_secondary, stringResource(auto_export_chosen_folder, folderName), statusText)
}

private fun formatTimestamp(epochMillis: Long): String {
  val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
  return formatter.format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()))
}
