@file:OptIn(ExperimentalMaterialApi::class)

package br.com.colman.petals.settings.view.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R

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
            }) { Text(text = selectedOption) }
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
