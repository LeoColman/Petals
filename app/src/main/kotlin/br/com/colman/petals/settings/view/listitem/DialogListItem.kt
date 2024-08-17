
package br.com.colman.petals.settings.view.listitem

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
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
    leadingContent = { Icon(icon, null, Modifier.size(42.dp)) },
    supportingContent = { Text(stringResource(descriptionId)) },
    headlineContent = { Text(stringResource(textId)) }
  )
  if (showDialog) {
    dialog { showDialog = false }
  }
}
