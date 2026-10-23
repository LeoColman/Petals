package br.com.colman.petals.components

import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@Composable
fun ClickableTextField(
  @StringRes label: Int,
  leadingIcon: ImageVector,
  value: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val currentOnClick by rememberUpdatedState(onClick)
  // Clicks when a tap ends, not when a finger lands. A press starts every touch, a scroll included, so clicking on it
  // opened the picker under a finger that only meant to scroll the form past this field. A scroll ends the press with
  // a cancel instead of a release.
  LaunchedEffect(interactionSource) {
    interactionSource.interactions.collect { if (it is PressInteraction.Release) currentOnClick() }
  }

  OutlinedTextField(
    value = value,
    onValueChange = {},
    leadingIcon = { Icon(leadingIcon, null) },
    label = { Text(stringResource(label)) },
    readOnly = true,
    interactionSource = interactionSource,
    modifier = modifier
  )
}
