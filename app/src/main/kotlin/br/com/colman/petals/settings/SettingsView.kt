@file:OptIn(ExperimentalMaterialApi::class)

package br.com.colman.petals.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.currency_icon
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.what_icon_should_be_used_for_currency
import compose.icons.TablerIcons
import compose.icons.tablericons.Cash

@Composable
fun SettingsView(settingsRepository: SettingsRepository) {
  val currentCurrency by settingsRepository.currencyIcon.collectAsState("$")
  val setCurrency = settingsRepository::setCurrencyIcon

  Column {
    CurrencyListItem(currentCurrency, setCurrency)
  }
}

@Preview
@Composable
private fun CurrencyListItem(
  currency: String = "",
  setCurrency: (String) -> Unit = {}
) {
  var shouldShowDialog by remember { mutableStateOf(false) }

  ListItem(
    modifier = Modifier.clickable { shouldShowDialog = true },
    icon = { Icon(TablerIcons.Cash, null, Modifier.size(42.dp)) },

    secondaryText = { Text(stringResource(what_icon_should_be_used_for_currency)) }
  ) {
    Text(stringResource(currency_icon))
  }

  if (shouldShowDialog) {
    CurrencyDialog(currency, setCurrency) { shouldShowDialog = false }
  }
}

@Preview
@Composable
private fun CurrencyDialog(
  currency: String = "$",
  setCurrency: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  var currency by remember { mutableStateOf(currency) }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      OutlinedTextField(currency, { currency = it }, label = { Text(stringResource(currency_icon)) })
    },
    confirmButton = {
      Text(
        stringResource(ok),
        Modifier
          .padding(8.dp)
          .clickable { setCurrency(currency); onDismiss() }
      )
    }
  )
}
