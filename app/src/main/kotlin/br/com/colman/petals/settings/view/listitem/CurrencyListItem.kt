package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R
import br.com.colman.petals.R.string
import br.com.colman.petals.settings.view.dialog.TextfieldDialog
import compose.icons.TablerIcons
import compose.icons.tablericons.Cash

@Preview
@Composable
fun CurrencyListItem(currency: String = "", setCurrency: (String) -> Unit = {}) {
  DialogListItem(
    icon = TablerIcons.Cash,
    textId = R.string.currency_icon,
    descriptionId = R.string.what_icon_should_be_used_for_currency,
    dialog = { hideDialog -> CurrencyDialog(currency, setCurrency, hideDialog) }
  )
}

@Preview
@Composable
private fun CurrencyDialog(
  initialCurrencyFormat: String = "$",
  setCurrencyFormat: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  TextfieldDialog(
    initialValue = initialCurrencyFormat,
    setValue = setCurrencyFormat,
    onDismiss = onDismiss,
    resourceLabel = string.currency_icon
  )
}
