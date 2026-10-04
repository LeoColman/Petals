package br.com.colman.petals.strain

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.add_strain
import br.com.colman.petals.R.string.cbd_percent
import br.com.colman.petals.R.string.cost_invalid
import br.com.colman.petals.R.string.cost_per_gram_title
import br.com.colman.petals.R.string.edit_strain
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.percent_out_of_range
import br.com.colman.petals.R.string.strain_name
import br.com.colman.petals.R.string.strain_name_taken
import br.com.colman.petals.R.string.thc_percent
import br.com.colman.petals.strain.StrainProblem.CbdOutOfRange
import br.com.colman.petals.strain.StrainProblem.CostInvalid
import br.com.colman.petals.strain.StrainProblem.NameTaken
import br.com.colman.petals.strain.StrainProblem.ThcOutOfRange
import br.com.colman.petals.strain.repository.Strain
import compose.icons.TablerIcons
import compose.icons.tablericons.Cash
import compose.icons.tablericons.Leaf
import compose.icons.tablericons.Percentage

/**
 * Creates a strain, or edits [editing]. OK stays disabled until the form makes a strain; a missing name only does
 * that, while a taken name or an unreadable number also says why under its field.
 */
@Composable
fun StrainDialog(
  catalog: List<Strain>,
  editing: Strain? = null,
  onDismiss: () -> Unit = {},
  onSave: (Strain) -> Unit = {}
) {
  var draft by remember { mutableStateOf(editing?.let(::StrainDraft) ?: StrainDraft()) }
  val problems = draft.problems(catalog, editing)

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(if (editing == null) add_strain else edit_strain)) },
    text = {
      Column(Modifier.fillMaxWidth(), spacedBy(8.dp)) {
        StrainTextField(draft.name, strain_name, TablerIcons.Leaf, (NameTaken in problems).then(strain_name_taken)) {
          draft = draft.copy(name = it)
        }
        NumberField(draft.thcPercent, thc_percent, TablerIcons.Percentage, ThcOutOfRange in problems) {
          draft = draft.copy(thcPercent = it)
        }
        NumberField(draft.cbdPercent, cbd_percent, TablerIcons.Percentage, CbdOutOfRange in problems) {
          draft = draft.copy(cbdPercent = it)
        }
        StrainTextField(
          draft.costPerGram,
          cost_per_gram_title,
          TablerIcons.Cash,
          (CostInvalid in problems).then(cost_invalid),
          KeyboardType.Decimal
        ) { draft = draft.copy(costPerGram = it) }
      }
    },
    confirmButton = {
      TextButton({ onSave(draft.toStrain(editing)) }, enabled = problems.isEmpty()) {
        Text(stringResource(ok))
      }
    }
  )
}

@Composable
private fun NumberField(
  value: String,
  @StringRes label: Int,
  icon: ImageVector,
  isOutOfRange: Boolean,
  onValueChange: (String) -> Unit
) = StrainTextField(value, label, icon, isOutOfRange.then(percent_out_of_range), KeyboardType.Decimal, onValueChange)

@Suppress("LongParameterList")
@Composable
private fun StrainTextField(
  value: String,
  @StringRes label: Int,
  icon: ImageVector,
  @StringRes error: Int?,
  keyboardType: KeyboardType = KeyboardType.Text,
  onValueChange: (String) -> Unit
) {
  Column {
    OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      leadingIcon = { Icon(icon, null) },
      label = { Text(stringResource(label)) },
      isError = error != null,
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
      modifier = Modifier.fillMaxWidth()
    )
    if (error != null) {
      Text(stringResource(error), color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
    }
  }
}

private fun Boolean.then(@StringRes error: Int): Int? = if (this) error else null
