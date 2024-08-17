package br.com.colman.petals.settings.view.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectFromListDialog(
  initialValue: String,
  possibleValues: List<String>,
  setValue: (String) -> Unit,
  onDismiss: () -> Unit,
  @StringRes label: Int,
) {
  var value by remember { mutableStateOf(initialValue) }
  var expanded by remember { mutableStateOf(false) }
  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
      ) {
        TextField(
          value = value,
          onValueChange = { },
          readOnly = true,
          label = { Text(text = stringResource(label)) },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
          colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false }
        ) {
          possibleValues.forEach { selectedOption ->
            DropdownMenuItem(onClick = {
              value = selectedOption
              expanded = false
            }, text = { Text(text = selectedOption) })
          }
        }
      }
    },
    confirmButton = {
      Text(
        stringResource(R.string.ok),
        Modifier
          .padding(8.dp)
          .clickable {
            setValue(value)
            onDismiss()
          }
      )
    }
  )
}
