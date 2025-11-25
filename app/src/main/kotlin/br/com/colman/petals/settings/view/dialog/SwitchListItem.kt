package br.com.colman.petals.settings.view.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.semantics


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwitchListItem(
  icon: ImageVector,
  @StringRes textId: Int,
  @StringRes descriptionId: Int,
  initialState: Boolean,
  onChangeState: (Boolean) -> Unit,
) {
  Row(
    modifier = Modifier.semantics(mergeDescendants = true) {}
  ) {
    ListItem(
      modifier = Modifier.fillMaxWidth().weight(0.7f),
      icon = { Icon(icon, null, Modifier.size(42.dp)) },
      secondaryText = { Text(stringResource(descriptionId)) }
    ) {
      Text(stringResource(textId))
    }
    Switch(initialState, onChangeState, modifier = Modifier.align(CenterVertically).padding(end = 8.dp))
  }
}

