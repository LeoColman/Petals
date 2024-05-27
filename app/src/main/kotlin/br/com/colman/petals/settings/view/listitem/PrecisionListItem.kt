package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.decimal_precision
import br.com.colman.petals.R.string.what_decimal_precision_should_be_used
import br.com.colman.petals.settings.view.dialog.SelectFromListDialog
import compose.icons.TablerIcons
import compose.icons.tablericons.Calculator

@Preview
@Composable
fun PrecisionListItem(
  decimalPrecision: Int = 2,
  decimalPrecisionList: List<Int> = listOf(),
  setDecimalPrecision: (Int) -> Unit = {}
) {
  DialogListItem(
    icon = TablerIcons.Calculator,
    textId = decimal_precision,
    descriptionId = what_decimal_precision_should_be_used,
    dialog = { hideDialog -> PrecisionDialog(decimalPrecision, decimalPrecisionList, setDecimalPrecision, hideDialog) }
  )
}

@Preview
@Composable
private fun PrecisionDialog(
  initialDecimalPrecision: Int = 2,
  decimalPrecisionList: List<Int> = listOf(),
  setDecimalPrecision: (Int) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  SelectFromListDialog(
    initialValue = initialDecimalPrecision.toString(),
    possibleValues = decimalPrecisionList.map { "$it" },
    setValue = { setDecimalPrecision(it.toInt()) },
    onDismiss = onDismiss,
    label = decimal_precision
  )
}
