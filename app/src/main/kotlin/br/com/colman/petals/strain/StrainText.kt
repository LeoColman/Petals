package br.com.colman.petals.strain

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.string.strain_archived
import br.com.colman.petals.R.string.strain_cbd
import br.com.colman.petals.R.string.strain_thc
import br.com.colman.petals.strain.repository.Strain

/** The strain's name, marked when it is archived. */
@Composable
fun Strain.displayName(): String = if (isArchived) stringResource(strain_archived, name) else name

/** "THC 27% · CBD 1%", with whichever potencies the strain has, or null when it has none. */
@Composable
fun Strain.potencyText(): String? = listOfNotNull(
  thcPercent?.let { stringResource(strain_thc, it.toPlainString()) },
  cbdPercent?.let { stringResource(strain_cbd, it.toPlainString()) }
).takeIf { it.isNotEmpty() }?.joinToString(" · ")
