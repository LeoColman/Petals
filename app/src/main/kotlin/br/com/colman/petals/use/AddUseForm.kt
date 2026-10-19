package br.com.colman.petals.use

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import br.com.colman.petals.R.string
import br.com.colman.petals.R.string.add_use
import br.com.colman.petals.R.string.amount_grams_title
import br.com.colman.petals.R.string.because_i_wanted
import br.com.colman.petals.R.string.consumption_method
import br.com.colman.petals.R.string.cost_per_gram_title
import br.com.colman.petals.R.string.method_unspecified
import br.com.colman.petals.components.ClickableTextField
import br.com.colman.petals.components.StarRating
import br.com.colman.petals.components.dateDialogState
import br.com.colman.petals.components.timeDialogState
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.strain.StrainField
import br.com.colman.petals.strain.repository.StrainRepository
import br.com.colman.petals.use.repository.ConsumptionMethod
import br.com.colman.petals.utils.truncatedToMinute
import compose.icons.TablerIcons
import compose.icons.tablericons.Calendar
import compose.icons.tablericons.Cash
import compose.icons.tablericons.Clock
import compose.icons.tablericons.Flame
import compose.icons.tablericons.Notebook
import compose.icons.tablericons.Scale
import org.koin.compose.koinInject
import java.time.LocalDate
import java.time.LocalTime

@Suppress("NAME_SHADOWING", "LongParameterList")
@Composable
fun AddUseForm(
  amount: MutableState<String>,
  cost: MutableState<String>,
  date: MutableState<LocalDate>,
  time: MutableState<LocalTime>,
  description: MutableState<String>,
  consumptionMethod: MutableState<ConsumptionMethod?>,
  strainId: MutableState<String?>,
  rating: MutableState<Double?>,
  fillsStrainCost: Boolean = true,
) {
  val settingsRepository = koinInject<SettingsRepository>()
  val is24HoursFormat by settingsRepository.is24HoursFormat.collectAsState(false)

  var amount by amount
  var cost by cost

  var date by date
  val dateDialog = dateDialogState { date = it }

  var time by time
  val timeDialog = timeDialogState(is24HoursFormat = is24HoursFormat) { time = it }

  var description by description

  Column(Modifier.verticalScroll(rememberScrollState()), Arrangement.spacedBy(8.dp)) {
    Text(stringResource(add_use), fontWeight = Bold, fontSize = 16.sp)

    // Picking a strain fills in its cost for a new use, but never for one being edited: a use keeps the cost it was
    // logged at, so tagging an old use with a strain doesn't rewrite what it cost.
    UseStrainField(strainId) { if (fillsStrainCost) cost = it }

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

    OutlinedTextField(
      value = description,
      onValueChange = { description = it },
      leadingIcon = { Icon(TablerIcons.Notebook, null) },
      label = { Text(stringResource(string.description)) },
      placeholder = { Text(stringResource(because_i_wanted)) },
      minLines = 3
    )

    RatingField(rating)

    ConsumptionMethodField(consumptionMethod)
  }
}

/**
 * The dialog that adds or edits a use, holding [form]. It makes room for the keyboard: the dialog shrinks to the space
 * above it, so the form scrolls and OK stays in reach. Left to the system, the dialog keeps its height and slides up
 * under the keyboard, which hides the stars, the consumption method and OK, and the form can't scroll to them because
 * it still fits. Asking the window to resize for the keyboard (SOFT_INPUT_ADJUST_RESIZE) did nothing on Android 16, so
 * the dialog takes the keyboard's insets itself.
 */
@Composable
fun UseFormDialog(onDismiss: () -> Unit, confirmButton: @Composable () -> Unit, form: @Composable () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    text = form,
    confirmButton = confirmButton,
    // Clear of the status and navigation bars too, as the dialog's window now reaches under them.
    modifier = Modifier.safeDrawingPadding(),
    properties = DialogProperties(decorFitsSystemWindows = false)
  )
}

/** The strain picker, over the whole catalog, filling in the cost of whichever strain is picked or made. */
@Composable
private fun UseStrainField(strainId: MutableState<String?>, setCost: (String) -> Unit) {
  val strainRepository = koinInject<StrainRepository>()
  val catalog by remember(strainRepository) { strainRepository.all() }.collectAsState(emptyList())

  StrainField(
    strainId,
    catalog,
    onPick = { strain -> strain?.costPerGram?.let { setCost(it.toPlainString()) } },
    onCreate = strainRepository::upsert
  )
}

/**
 * How the use went, in stars; tapping the rating it has clears it. The label sits above the stars, so a long one never
 * squeezes their touch targets.
 */
@Composable
private fun RatingField(rating: MutableState<Double?>) {
  var stars by rating
  val label = stringResource(string.rating)

  Column(Modifier.fillMaxWidth()) {
    // The stars carry the label for screen readers, so they don't hear it twice.
    Text(label, Modifier.clearAndSetSemantics { })
    StarRating(stars, starSize = 36.dp, label = label) { stars = it }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ConsumptionMethodField(consumptionMethod: MutableState<ConsumptionMethod?>) {
  var selected by consumptionMethod
  var expanded by remember { mutableStateOf(false) }

  val selectedLabel = selected?.let { stringResource(it.label) } ?: stringResource(method_unspecified)

  ExposedDropdownMenuBox(expanded, { expanded = it }, Modifier.fillMaxWidth()) {
    OutlinedTextField(
      value = selectedLabel,
      onValueChange = {},
      readOnly = true,
      leadingIcon = { Icon(TablerIcons.Flame, null) },
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
      label = { Text(stringResource(consumption_method)) },
      modifier = Modifier.fillMaxWidth()
    )

    ExposedDropdownMenu(expanded, { expanded = false }) {
      DropdownMenuItem(onClick = {
        selected = null
        expanded = false
      }) {
        Text(stringResource(method_unspecified))
      }
      ConsumptionMethod.entries.forEach { method ->
        DropdownMenuItem(onClick = {
          selected = method
          expanded = false
        }) {
          Text(stringResource(method.label))
        }
      }
    }
  }
}
