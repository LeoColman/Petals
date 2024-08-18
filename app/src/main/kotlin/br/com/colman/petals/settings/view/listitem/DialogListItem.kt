@file:OptIn(ExperimentalMaterialApi::class)

package br.com.colman.petals.settings.view.listitem

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun DialogListItem(
  icon: ImageVector,
  @StringRes textId: Int,
  @StringRes descriptionId: Int,
  dialog: @Composable (hideDialog: () -> Unit) -> Unit
) {
  var showDialog by remember { mutableStateOf(false) }
  ListItem(
    modifier = Modifier.clickable { showDialog = true },
    icon = { Icon(icon, null, Modifier.size(42.dp)) },
    secondaryText = { Text(stringResource(descriptionId)) }
  ) {
    Text(stringResource(textId))
  }
  if (showDialog) {
    dialog { showDialog = false }
  }
}
