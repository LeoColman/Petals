package br.com.colman.petals.use

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import br.com.colman.petals.R.string
import br.com.colman.petals.R.string.add_use
import br.com.colman.petals.R.string.amount_grams_title
import br.com.colman.petals.R.string.cost_per_gram_title
import br.com.colman.petals.components.ClickableTextField
import br.com.colman.petals.components.dateDialogState
import br.com.colman.petals.components.timeDialogState
import br.com.colman.petals.utils.truncatedToMinute
import compose.icons.TablerIcons
import compose.icons.tablericons.Calendar
import compose.icons.tablericons.Cash
import compose.icons.tablericons.Clock
import compose.icons.tablericons.Scale
import java.time.LocalDate
import java.time.LocalTime

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
  val dateDialog = dateDialogState { date = it }

  var time by time
  val timeDialog = timeDialogState { time = it }

  Column(Modifier, Arrangement.spacedBy(8.dp)) {
    Text(stringResource(add_use), fontWeight = Bold, fontSize = 16.sp)

    OutlinedTextField(
      value = amount,
      onValueChange = { amount = it },
      leadingIcon = { Icon(TablerIcons.Scale, null) },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      label = { Text(stringResource(amount_grams_title)) },
      placeholder = { Text("0.25") }
    )

    OutlinedTextField(
      value = cost,
      onValueChange = { cost = it },
      leadingIcon = { Icon(TablerIcons.Cash, null) },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      label = { Text(stringResource(cost_per_gram_title)) },
      placeholder = { Text("18.00") }
    )

    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
      Box(Modifier.weight(0.55f)) {
        ClickableTextField(label = string.date, leadingIcon = TablerIcons.Calendar, value = "$date") {
          dateDialog.show()
        }
      }
      Box(Modifier.weight(0.45f)) {
        ClickableTextField(
          label = string.time,
          leadingIcon = TablerIcons.Clock,
          value = "${time.truncatedToMinute()}"
        ) {
          timeDialog.show()
        }
      }
    }
  }
}
