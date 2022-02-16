package br.com.colman.petals.use

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.see_more
import br.com.colman.petals.use.repository.Use
import compose.icons.TablerIcons
import compose.icons.tablericons.*
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
  val (amountGrams, costPerGram, date) = use
  val dateString = date.format(ofPattern("yyyy/MM/dd"))
  val timeString = date.format(ofPattern("HH:mm"))

  Card(Modifier.padding(8.dp).fillMaxWidth(), elevation = 6.dp) {
    Row(Modifier.fillMaxWidth(), spacedBy(8.dp), CenterVertically) {

      Column(Modifier.padding(24.dp).weight(0.7F), spacedBy(16.dp)) {
        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.Smoking, null)
          Text("$dateString at $timeString")
        }

        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.CurrencyDollar, null)
          val costPerGramString = costPerGram.setScale(3, HALF_UP).toString()
          Text(stringResource(R.string.cost_per_gram, costPerGramString))
        }

        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.Scale, null)
          val amountGramsString = amountGrams.setScale(3, HALF_UP).toString()
          Text(stringResource(R.string.amount_grams, amountGramsString))
        }

        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.ReportMoney, null)
          val total = (amountGrams * costPerGram).setScale(3, HALF_UP)
          Text(stringResource(R.string.total_spent, total))
        }
      }

      Column(Modifier.padding(24.dp).weight(0.3F), spacedBy(24.dp), Alignment.End) {
        EditDialogButton(use, onEditUse)

        DeleteUseButton(use, onDeleteUse)
      }
    }
  }
}

@Preview
@Composable
private fun DeleteUseButton(use: Use = Use(), onDeleteUse: (Use) -> Unit = {}) {
  Icon(TablerIcons.Trash, null, Modifier.clickable { onDeleteUse(use) })
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
  val date = remember { mutableStateOf(use.date.toLocalDate()) }
  val time = remember { mutableStateOf(use.date.toLocalTime()) }

  val use = Use(
    amount.value.toBigDecimalOrNull() ?: BigDecimal.ZERO,
    costPerGram.value.toBigDecimalOrNull() ?: BigDecimal.ZERO,
    LocalDateTime.of(date.value, time.value),
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
