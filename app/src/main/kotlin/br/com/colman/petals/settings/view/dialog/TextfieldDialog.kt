package br.com.colman.petals.settings.view.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
fun TextfieldDialog(
  initialValue: String,
  setValue: (String) -> Unit,
  onDismiss: () -> Unit,
  resourceLabel: Int,
) {
  var value by remember { mutableStateOf(initialValue) }
  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      OutlinedTextField(
        value,
        { value = it },
        label = { Text(stringResource(resourceLabel)) }
      )
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
