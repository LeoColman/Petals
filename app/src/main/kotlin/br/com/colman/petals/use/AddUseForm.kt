package br.com.colman.petals.use

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R
import br.com.colman.petals.R.string.add_use
import br.com.colman.petals.R.string.amount_grams
import br.com.colman.petals.R.string.cost_per_gram
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.select_date
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogScope
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import compose.icons.TablerIcons
import compose.icons.tablericons.Calendar
import compose.icons.tablericons.Clock
import compose.icons.tablericons.Scale
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Suppress("NAME_SHADOWING")
@Composable
fun AddUseForm(
  amount: MutableState<String>,
  cost: MutableState<String>,
  date: MutableState<LocalDate>,
  time: MutableState<LocalTime>,
) {
  var amount by amount
  var cost by cost

  var date by date
  val dateDialog = dateDialog { date = it }

  var time by time
  val timeDialog = timeDialog { time = it }

  Column(Modifier, Arrangement.spacedBy(8.dp)) {
    Text(stringResource(add_use), fontWeight = Bold, fontSize = 16.sp)

    OutlinedTextField(
      value = amount,
      onValueChange = { amount = it },
      leadingIcon = { Icon(TablerIcons.Scale, null) },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      label = { Text(stringResource(amount_grams)) },
      placeholder = { Text("0.25") }
    )

    OutlinedTextField(
      value = cost,
      onValueChange = { cost = it },
      leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      label = { Text(stringResource(cost_per_gram)) },
      placeholder = { Text("18.00") }
    )

    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
      OutlinedTextField(
        modifier = Modifier
          .weight(0.5f)
          .clickable { dateDialog.show() },
        value = date.format(DateTimeFormatter.ofPattern("yy/MM/dd")),
        onValueChange = {},
        leadingIcon = { Icon(TablerIcons.Calendar, null) },
        enabled = false,
        label = { Text(stringResource(R.string.date)) }
      )


      OutlinedTextField(
        modifier = Modifier
          .weight(0.5f)
          .clickable { timeDialog.show() },
        value = time.format(DateTimeFormatter.ofPattern("HH:mm")),
        onValueChange = {},
        leadingIcon = { Icon(TablerIcons.Clock, null) },
        enabled = false,
        label = { Text(stringResource(R.string.time)) }
      )
    }
  }
}

@Composable
private fun dateDialog(onDateChange: (LocalDate) -> Unit) = myMaterialDialog {
  datepicker(title = stringResource(select_date)) { date ->
    onDateChange(date)
  }
}

@Composable
private fun timeDialog(onTimeChange: (LocalTime) -> Unit) = myMaterialDialog {
  timepicker { onTimeChange(it) }
}

@Composable
private fun myMaterialDialog(content: @Composable MaterialDialogScope.() -> Unit) = rememberMaterialDialogState().also {
  MaterialDialog(
    dialogState = it,
    buttons = {
      positiveButton(stringResource(ok))
      negativeButton(stringResource(R.string.cancel))
    },
    content = content
  )
}