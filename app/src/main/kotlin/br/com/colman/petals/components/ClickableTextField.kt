package br.com.colman.petals.components

import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
  val isPressed by interactionSource.collectIsPressedAsState()
  if (isPressed) {
    onClick()
  }

  OutlinedTextField(
    value,
    onValueChange = {},
    leadingIcon = { Icon(leadingIcon, null) },
    label = { Text(stringResource(label)) },
    readOnly = true,
    interactionSource = interactionSource,
    modifier = modifier
  )
}