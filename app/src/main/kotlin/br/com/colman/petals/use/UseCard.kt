package br.com.colman.petals.use

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.CurrencyDollar
import compose.icons.tablericons.ReportMoney
import compose.icons.tablericons.Scale
import compose.icons.tablericons.Smoking
import compose.icons.tablericons.Trash
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern


@Preview
@Composable
fun UseCards(
  uses: List<Use> = List(5) { Use() },
  onEditUse: (Use) -> Unit = {},
  onDeleteUse: (Use) -> Unit = {},
) {
  Column(Modifier.fillMaxWidth(), spacedBy(8.dp)) {
    uses.sortedByDescending { it.date }.forEach {
      UseCard(it, onEditUse, onDeleteUse)
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
          val costPerGramString = costPerGram.setScale(2).toString()
          Text("$costPerGramString per gram")
        }

        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.Scale, null)
          val amountGramsString = amountGrams.setScale(2).toString()
          Text("$amountGramsString grams")
        }

        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.ReportMoney, null)
          val total = (amountGrams * costPerGram).setScale(2)
          Text("$total total")
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
    Text("Ok")
  }
}
