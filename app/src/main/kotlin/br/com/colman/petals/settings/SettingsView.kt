@file:OptIn(ExperimentalMaterialApi::class)

package br.com.colman.petals.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R
import br.com.colman.petals.R.string.*
import compose.icons.TablerIcons
import compose.icons.tablericons.BrandGithub
import compose.icons.tablericons.Calendar
import compose.icons.tablericons.Cash
import compose.icons.tablericons.Clock

@Composable
fun SettingsView(settingsRepository: SettingsRepository) {
  val currentCurrency by settingsRepository.currencyIcon.collectAsState("$")
  val setCurrency = settingsRepository::setCurrencyIcon
  val setDateFormat = settingsRepository::setDateFormat
  val dateFormatList = settingsRepository.dateFormatList
  val currentDateFormat by settingsRepository.dateFormat.collectAsState(dateFormatList[0])
  val setTimeFormat = settingsRepository::setTimeFormat
  val timeFormatList = settingsRepository.timeFormatList
  val currentTimeFormat by settingsRepository.timeFormat.collectAsState(timeFormatList[0])

  Column {
    CurrencyListItem(currentCurrency, setCurrency)
    RepositoryListItem()
    DateListItem(currentDateFormat, dateFormatList, setDateFormat)
    TimeListItem(currentTimeFormat, timeFormatList, setTimeFormat)
  }
}

@Preview
@Composable
private fun RepositoryListItem() {
  val uriHandler = LocalUriHandler.current
  val openUrl = { uriHandler.openUri("https://github.com/LeoColman/Petals") }

  ListItem(
    modifier = Modifier.clickable { openUrl() },
    icon = { Icon(TablerIcons.BrandGithub, null, Modifier.size(42.dp)) },
    secondaryText = { Text(stringResource(R.string.repository_link_description)) }
  ) {
    Text(stringResource(R.string.repository_link_title))
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
fun DateListItem(
  dateFormat: String  = "",
  dateFormatList: Array<String> = arrayOf(),
  setDateFormat: (String) -> Unit = {}
) {
  var shouldShowDialog by remember { mutableStateOf(false) }

  ListItem(
    modifier = Modifier.clickable { shouldShowDialog = true },
    icon = { Icon(TablerIcons.Calendar, null, Modifier.size(42.dp)) },

    secondaryText = { Text(stringResource(what_date_format_should_be_used)) }
  ) {
    Text(stringResource(date_format_label))
  }

  if (shouldShowDialog) {
    DateDialog(dateFormat, dateFormatList, setDateFormat) { shouldShowDialog = false }
  }
}

@Preview
@Composable
fun TimeListItem(
  timeFormat: String  = "",
  timeFormatList: Array<String> = arrayOf(),
  setTimeFormat: (String) -> Unit = {}
) {
  var shouldShowDialog by remember { mutableStateOf(false) }

  ListItem(
    modifier = Modifier.clickable { shouldShowDialog = true },
    icon = { Icon(TablerIcons.Clock, null, Modifier.size(42.dp)) },

    secondaryText = { Text(stringResource(what_time_format_should_be_used)) }
  ) {
    Text(stringResource(time_format_label))
  }

  if (shouldShowDialog) {
    TimeDialog(timeFormat, timeFormatList, setTimeFormat) { shouldShowDialog = false }
  }
}

@Preview
@Composable
private fun TimeDialog(
  timeFormat: String = "",
  timeFormatList: Array<String> = arrayOf(),
  setTimeFormat: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  var timeFormat by remember { mutableStateOf(timeFormat) }
  var expanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      ExposedDropdownMenuBox(
        expanded = false,
        onExpandedChange = { expanded = !expanded }) {

        TextField(
          value = timeFormat,
          onValueChange = {},
          readOnly = true,
          label = { Text(text = stringResource(time_format_label)) },
          trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(
              expanded = expanded
            )
          },
          colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false }
        ) {
          timeFormatList.forEach { selectedOption ->
            DropdownMenuItem(onClick = {
              timeFormat = selectedOption
              expanded = false
            }) {
              Text(text = selectedOption)
            }
          }
        }
      }
    },
    confirmButton = {
      Text(
        stringResource(ok),
        Modifier
          .padding(8.dp)
          .clickable { setTimeFormat(timeFormat); onDismiss() }
      )
    }
  )
}

@Preview
@Composable
private fun DateDialog(
  dateFormat: String = "",
  dateFormatList: Array<String> = arrayOf(),
  setDateFormat: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  var dateFormat by remember { mutableStateOf(dateFormat) }
  var expanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      ExposedDropdownMenuBox(
        expanded = false,
        onExpandedChange = { expanded = !expanded }) {

        TextField(
          value = dateFormat,
          onValueChange = {},
          readOnly = true,
          label = { Text(text = stringResource(date_format_label)) },
          trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(
              expanded = expanded
            )
          },
          colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false }
        ) {
          dateFormatList.forEach { selectedOption ->
            DropdownMenuItem(onClick = {
              dateFormat = selectedOption
              expanded = false
            }) {
              Text(text = selectedOption)
            }
          }
        }
      }
    },
    confirmButton = {
      Text(
        stringResource(ok),
        Modifier
          .padding(8.dp)
          .clickable { setDateFormat(dateFormat); onDismiss() }
      )
    }
  )
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
