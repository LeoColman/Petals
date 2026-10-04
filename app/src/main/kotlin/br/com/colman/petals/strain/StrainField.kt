package br.com.colman.petals.strain

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.string.new_strain
import br.com.colman.petals.R.string.no_strain
import br.com.colman.petals.R.string.strain
import br.com.colman.petals.strain.repository.Strain
import compose.icons.TablerIcons
import compose.icons.tablericons.Leaf

/**
 * Picks the strain a use is logged with from [catalog], or makes a new one there and then. Archived strains aren't
 * offered, though one a use already has still shows. [onPick] hears about every strain picked or made, and about
 * picking none, so the form can fill in the strain's cost.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StrainField(
  strainId: MutableState<String?>,
  catalog: List<Strain>,
  onPick: (Strain?) -> Unit = {},
  onCreate: (Strain) -> Unit = {}
) {
  var selectedId by strainId
  var expanded by remember { mutableStateOf(false) }
  var isCreating by remember { mutableStateOf(false) }
  val selected = catalog.find { it.id == selectedId }

  fun pick(strain: Strain?) {
    selectedId = strain?.id
    onPick(strain)
    expanded = false
  }

  ExposedDropdownMenuBox(expanded, { expanded = it }, Modifier.fillMaxWidth()) {
    OutlinedTextField(
      value = selected?.displayName() ?: stringResource(no_strain),
      onValueChange = {},
      readOnly = true,
      leadingIcon = { Icon(TablerIcons.Leaf, null) },
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
      label = { Text(stringResource(strain)) },
      modifier = Modifier.fillMaxWidth()
    )

    ExposedDropdownMenu(expanded, { expanded = false }) {
      DropdownMenuItem({ pick(null) }) { Text(stringResource(no_strain)) }
      catalog.filterNot { it.isArchived }.forEach { strain ->
        DropdownMenuItem({ pick(strain) }) {
          Column {
            Text(strain.name)
            strain.potencyText()?.let { Text(it, style = MaterialTheme.typography.caption) }
          }
        }
      }
      DropdownMenuItem({
        expanded = false
        isCreating = true
      }) { Text(stringResource(new_strain)) }
    }
  }

  if (isCreating) {
    StrainDialog(catalog, onDismiss = { isCreating = false }) { created ->
      onCreate(created)
      pick(created)
      isCreating = false
    }
  }
}

/**
 * The strain a new use starts with, copied from [previous]: none when that strain is archived or gone, since an
 * archived strain is one the user is done with and the picker doesn't offer it.
 */
fun newUseStrainId(previous: String?, catalog: List<Strain>): String? =
  catalog.find { it.id == previous && !it.isArchived }?.id
