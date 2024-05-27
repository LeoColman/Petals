package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.app_pin
import br.com.colman.petals.R.string.password_description
import br.com.colman.petals.settings.view.dialog.TextfieldDialog
import compose.icons.TablerIcons
import compose.icons.tablericons.Lock

@Preview
@Composable
fun PinListItem(setPin: (String?) -> Unit = {}) {
  DialogListItem(
    icon = TablerIcons.Lock,
    descriptionId = password_description,
    textId = app_pin,
    dialog = { hideDialog -> PinDialog(setPin, hideDialog) }
  )
}

@Preview
@Composable
private fun PinDialog(
  setPin: (String?) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  fun setValue(value: String) = if (value.isEmpty()) setPin(null) else setPin(value)

  TextfieldDialog(
    initialValue = "",
    setValue = { setValue(it) },
    onDismiss = onDismiss,
    resourceLabel = app_pin
  )
}
