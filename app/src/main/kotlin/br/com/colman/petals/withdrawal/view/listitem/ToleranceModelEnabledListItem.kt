package br.com.colman.petals.withdrawal.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.enable_or_disable_tolerance_model
import br.com.colman.petals.R.string.tolerance_model_enabled
import br.com.colman.petals.settings.view.dialog.SwitchListItem
import compose.icons.TablerIcons
import compose.icons.tablericons.Scale

@Preview
@Composable
fun ToleranceModelEnabledListItem(
  toleranceModelEnabled: Boolean = true,
  setToleranceModelEnabled: (Boolean) -> Unit = {}
) {
  SwitchListItem(
    icon = TablerIcons.Scale,
    textId = tolerance_model_enabled,
    descriptionId = enable_or_disable_tolerance_model,
    initialState = toleranceModelEnabled,
    onChangeState = setToleranceModelEnabled
  )
}
