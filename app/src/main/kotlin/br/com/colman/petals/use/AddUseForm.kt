package br.com.colman.petals.use

import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R.string
import br.com.colman.petals.R.string.add_use
import br.com.colman.petals.R.string.amount_grams_title
import br.com.colman.petals.R.string.cancel
import br.com.colman.petals.R.string.cost_per_gram_title
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
import compose.icons.tablericons.CurrencyDollar
import compose.icons.tablericons.Scale
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit.MINUTES

@Suppress("NAME_SHADOWING", "MagicNumber")
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
      label = { Text(stringResource(amount_grams_title)) },
      placeholder = { Text("0.25") }
    )

    OutlinedTextField(
      value = cost,
      onValueChange = { cost = it },
      leadingIcon = { Icon(TablerIcons.CurrencyDollar, null) },
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

@Composable
private fun ClickableTextField(
  @StringRes label: Int,
  leadingIcon: ImageVector,
  value: String,
  onClick: () -> Unit
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
    interactionSource = interactionSource
  )
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
      negativeButton(stringResource(cancel))
    },
    content = content
  )
}

private fun LocalTime.truncatedToMinute() = truncatedTo(MINUTES)
