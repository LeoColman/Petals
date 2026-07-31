package br.com.colman.petals.settings.view.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwitchListItem(
  icon: ImageVector,
  text: String,
  description: String,
  initialState: Boolean,
  onChangeState: (Boolean) -> Unit,
) {
  // One focus stop for the whole row. Toggleable alone is not enough: Material's ListItem merges its
  // own descendants, so the label stays on a node of its own and a screen reader still stops twice,
  // once for the text and once for a state with no name attached. Clearing the ListItem drops that
  // second stop, and the description carries the words it was holding. The Switch hands over its
  // onCheckedChange for the same reason.
  Row(
    Modifier
      .toggleable(value = initialState, role = Role.Switch, onValueChange = onChangeState)
      .semantics { contentDescription = "$text. $description" }
  ) {
    ListItem(
      modifier = Modifier.fillMaxWidth().weight(0.7f).clearAndSetSemantics { },
      icon = { Icon(icon, null, Modifier.size(42.dp)) },
      secondaryText = { Text(description) }
    ) {
      Text(text)
    }
    Switch(initialState, null, modifier = Modifier.align(CenterVertically).padding(end = 8.dp))
  }
}

@Composable
fun SwitchListItem(
  icon: ImageVector,
  @StringRes textId: Int,
  @StringRes descriptionId: Int,
  initialState: Boolean,
  onChangeState: (Boolean) -> Unit,
) {
  SwitchListItem(
    icon = icon,
    text = stringResource(textId),
    description = stringResource(descriptionId),
    initialState = initialState,
    onChangeState = onChangeState,
  )
}
