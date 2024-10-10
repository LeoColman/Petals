package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.language_label
import br.com.colman.petals.R.string.what_language_should_be_used
import br.com.colman.petals.settings.view.dialog.SelectFromListDialog
import compose.icons.TablerIcons
import compose.icons.tablericons.Language

@Preview
@Composable
fun LanguageListItem(
  appLanguage: String = "English",
  appLanguageList: List<String> = listOf(),
  setAppLanguage: (String) -> Unit = {}
) {
  DialogListItem(
    icon = TablerIcons.Language,
    textId = language_label,
    descriptionId = what_language_should_be_used,
    dialog = { hideDialog -> LanguageDialog(appLanguage, appLanguageList, setAppLanguage, hideDialog) }
  )
}

@Preview
@Composable
private fun LanguageDialog(
  initialAppLanguage: String = "",
  appLanguageList: List<String> = listOf(),
  setAppLanguage: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  SelectFromListDialog(
    initialValue = initialAppLanguage,
    possibleValues = appLanguageList,
    setValue = setAppLanguage,
    onDismiss = onDismiss,
    label = language_label
  )
}
