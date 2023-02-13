package br.com.colman.petals.use

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.*
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.repository.Use
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import org.koin.androidx.compose.get
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

@Preview
@Composable
fun UseCards(
  uses: List<Use> = List(5) { Use() },
  onEditUse: (Use) -> Unit = {},
  onDeleteUse: (Use) -> Unit = {},
) {
  var usesToShow by remember { mutableStateOf(5) }

  Column(Modifier.fillMaxWidth(), spacedBy(8.dp)) {
    uses.sortedByDescending { it.date }.take(usesToShow).forEach {
      UseCard(it, onEditUse, onDeleteUse)
    }

    Button({ usesToShow += 5 }, Modifier.align(CenterHorizontally)) {
      Text(stringResource(see_more))
    }
  }
}

@Preview
@Composable
fun UseCard(use: Use = Use(), onEditUse: (Use) -> Unit = { }, onDeleteUse: (Use) -> Unit = {}) {
  val (date, amountGrams, costPerGram) = use
  val settingsRepository = get<SettingsRepository>()
  val dateFormat by settingsRepository.dateFormat.collectAsState(settingsRepository.dateFormatList[0])
  val dateString = date.format(ofPattern(dateFormat))
  val timeFormat by settingsRepository.timeFormat.collectAsState(settingsRepository.timeFormatList[0])
  val timeString = date.format(ofPattern(timeFormat))
  val currencySymbol by get<SettingsRepository>().currencyIcon.collectAsState("$")

  Card(
    Modifier
      .padding(8.dp)
      .fillMaxWidth(),
    elevation = 6.dp
  ) {
    Row(Modifier.fillMaxWidth(), spacedBy(8.dp), CenterVertically) {
      Column(
        Modifier
          .padding(24.dp)
          .weight(0.7F),
        spacedBy(16.dp)
      ) {
        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.Smoking, null)
          Text(stringResource(date_at_time, dateString, timeString))
        }

        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.Cash, null)
          val costPerGramString = costPerGram.setScale(3, HALF_UP).toString()
          Text("$currencySymbol " + stringResource(cost_per_gram, costPerGramString))
        }

        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.Scale, null)
          val amountGramsString = amountGrams.setScale(3, HALF_UP).toString()
          Text(stringResource(amount_grams, amountGramsString))
        }

        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.ReportMoney, null)
          val total = (amountGrams * costPerGram).setScale(3, HALF_UP)
          Text("$currencySymbol " + stringResource(total_spent, total))
        }
      }

      Column(
        Modifier
          .padding(24.dp)
          .weight(0.3F),
        spacedBy(24.dp),
        Alignment.End
      ) {
        EditDialogButton(use, onEditUse)

        DeleteUseButton(use, onDeleteUse)
      }
    }
  }
}

@Preview
@Composable
private fun DeleteUseButton(use: Use = Use(), onDeleteUse: (Use) -> Unit = {}, context: Context = LocalContext.current) {
  val showDialog = remember { mutableStateOf(false) }
  val dateString = use.date.format(ofPattern("yyyy-MM-dd"))
  val timeString = use.date.format(ofPattern("HH:mm"))
  Icon(TablerIcons.Trash, null, Modifier.clickable { showDialog.value = true })

  if(showDialog.value){
    AlertDialog(
      onDismissRequest = {showDialog.value = false},
      confirmButton = {
        Button(onClick = {
          onDeleteUse(use)
          showDialog.value = false
          Toast.makeText(context, deleted_successfully, Toast.LENGTH_SHORT).show() }, colors = ButtonDefaults.buttonColors(Color.White)) {
          Text(text = stringResource(yes))
        }
      },
      title = {
        Row(verticalAlignment = CenterVertically) {
          Text(text = stringResource(delete))
          Icon(imageVector = Icons.Outlined.Delete, contentDescription = stringResource(delete))
        }
      },
      text = {
        Text(
          text = stringResource(confirm_to_delete) +" "+ stringResource(date_at_time, dateString, timeString)+"?"
        )
      },
      dismissButton = { Button(onClick = { showDialog.value = false }, colors = ButtonDefaults.buttonColors(Color.White)) { Text(text = stringResource(no)) } }
    )
  }
}

@Composable
private fun EditDialogButton(use: Use, onEditUse: (Use) -> Unit) {
  var openDialog by remember { mutableStateOf(false) }
  if (openDialog) {
    EditUseDialog(use, onEditUse) { openDialog = false }
  }
  Icon(Icons.Default.Edit, null, Modifier.clickable { openDialog = true })
}

@Suppress("NAME_SHADOWING")
@Composable
@Preview
private fun EditUseDialog(
  use: Use = Use(),
  onEditUse: (Use) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  val amount = remember { mutableStateOf(use.amountGrams.toString()) }
  val costPerGram = remember { mutableStateOf(use.costPerGram.toString()) }
  val date = remember { mutableStateOf(use.localDate) }
  val time = remember { mutableStateOf(use.date.toLocalTime()) }

  val use = Use(
    LocalDateTime.of(date.value, time.value),
    amount.value.toBigDecimalOrNull() ?: BigDecimal.ZERO,
    costPerGram.value.toBigDecimalOrNull() ?: BigDecimal.ZERO,
    use.id
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    text = { AddUseForm(amount, costPerGram, date, time) },
    confirmButton = { ConfirmEdit(onEditUse, use, onDismiss) }
  )
}

@Composable
private fun ConfirmEdit(
  onAddUse: (Use) -> Unit = {},
  use: Use = Use(),
  onDismiss: () -> Unit = {},
) {
  TextButton({ onAddUse(use); onDismiss() }) {
    Text(stringResource(ok))
  }
}
