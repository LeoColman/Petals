package br.com.colman.petals.use

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.drawable.ic_cannabis
import br.com.colman.petals.R.string.amount_grams
import br.com.colman.petals.R.string.confirm_to_delete
import br.com.colman.petals.R.string.cost_per_gram
import br.com.colman.petals.R.string.date_at_time
import br.com.colman.petals.R.string.delete_use_title
import br.com.colman.petals.R.string.deleted_successfully
import br.com.colman.petals.R.string.no
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.see_more
import br.com.colman.petals.R.string.total_spent
import br.com.colman.petals.R.string.yes
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.utils.truncatedToMinute
import compose.icons.TablerIcons
import compose.icons.tablericons.Cash
import compose.icons.tablericons.Notebook
import compose.icons.tablericons.ReportMoney
import compose.icons.tablericons.Scale
import compose.icons.tablericons.Trash
import org.koin.compose.koinInject
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE
import java.time.format.DateTimeFormatter.ISO_TIME
import java.time.format.DateTimeFormatter.ofPattern

@Preview
@Composable
fun UseCards(
  uses: List<Use> = List(5) { Use() },
  onEditUse: (Use) -> Unit = {},
  onDeleteUse: (Use) -> Unit = {},
) {
  var usesToShow by remember { mutableIntStateOf(5) }

  Column(Modifier.fillMaxWidth(), spacedBy(8.dp)) {
    uses.sortedByDescending { it.date }.take(usesToShow).forEach {
      UseCard(it, onEditUse, onDeleteUse)
    }

    Button({ usesToShow += 5 }, Modifier.align(CenterHorizontally)) {
      Text(stringResource(see_more))
    }
  }
}

@Suppress("LongMethod")
@Preview
@Composable
fun UseCard(use: Use = Use(), onEditUse: (Use) -> Unit = { }, onDeleteUse: (Use) -> Unit = {}) {
  val (date, amountGrams, costPerGram) = use
  val settingsRepository = koinInject<SettingsRepository>()
  val dateFormat by settingsRepository.dateFormat.collectAsState(settingsRepository.dateFormatList[0])
  val dateString = date.format(ofPattern(dateFormat))
  val timeFormat by settingsRepository.timeFormat.collectAsState(settingsRepository.timeFormatList[0])
  val timeString = date.format(ofPattern(timeFormat))
  val currencySymbol by koinInject<SettingsRepository>().currencyIcon.collectAsState("$")
  val decimalPrecision by settingsRepository.decimalPrecision.collectAsState(settingsRepository.decimalPrecisionList[2])

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
          Icon(ImageVector.vectorResource(ic_cannabis), null)
          Text(stringResource(date_at_time, dateString, timeString))
        }

        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.Cash, null)
          val costPerGramString = costPerGram.setScale(decimalPrecision, HALF_UP).toString()
          Text("$currencySymbol " + stringResource(cost_per_gram, costPerGramString))
        }

        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.Scale, null)
          val amountGramsString = amountGrams.setScale(decimalPrecision, HALF_UP).toString()
          Text(stringResource(amount_grams, amountGramsString))
        }

        Row(Modifier, spacedBy(8.dp), CenterVertically) {
          Icon(TablerIcons.ReportMoney, null)
          val total = (amountGrams * costPerGram).setScale(decimalPrecision, HALF_UP)
          Text("$currencySymbol " + stringResource(total_spent, total))
        }

        if (use.description.isNotBlank()) {
          Row(Modifier, spacedBy(8.dp), CenterVertically) {
            Icon(TablerIcons.Notebook, null)
            Text(use.description)
          }
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
private fun DeleteUseButton(
  use: Use = Use(),
  onDeleteUse: (Use) -> Unit = {},
  context: Context = LocalContext.current
) {
  val showDialog = remember { mutableStateOf(false) }
  // FIXME Date and Time string should come from Settings
  val dateString = use.date.format(ISO_DATE)
  val timeString = use.date.truncatedToMinute().format(ISO_TIME)
  Icon(TablerIcons.Trash, null, Modifier.clickable { showDialog.value = true })

  if (showDialog.value) {
    AlertDialog(onDismissRequest = { showDialog.value = false }, confirmButton = {
      TextButton({
        onDeleteUse(use)
        showDialog.value = false
        Toast.makeText(context, deleted_successfully, Toast.LENGTH_SHORT).show()
      }) {
        Text(text = stringResource(yes))
      }
    }, title = { Text(text = stringResource(delete_use_title)) }, text = {
      Text(
        text = stringResource(
          confirm_to_delete,
          stringResource(date_at_time, dateString, timeString)
        )
      )
    }, dismissButton = {
      TextButton({ showDialog.value = false }) {
        Text(text = stringResource(no))
      }
    })
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
  val description = remember { mutableStateOf(use.description) }

  val use = Use(
    LocalDateTime.of(date.value, time.value),
    amount.value.toBigDecimalOrNull() ?: BigDecimal.ZERO,
    costPerGram.value.toBigDecimalOrNull() ?: BigDecimal.ZERO,
    use.id,
    description.value
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    text = { AddUseForm(amount, costPerGram, date, time, description) },
    confirmButton = { ConfirmEdit(onEditUse, use, onDismiss) }
  )
}

@Composable
private fun ConfirmEdit(
  onAddUse: (Use) -> Unit = {},
  use: Use = Use(),
  onDismiss: () -> Unit = {},
) {
  TextButton({
    onAddUse(use)
    onDismiss()
  }) {
    Text(stringResource(ok))
  }
}
