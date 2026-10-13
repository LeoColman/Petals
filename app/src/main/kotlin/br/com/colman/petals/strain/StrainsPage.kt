package br.com.colman.petals.strain

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R.plurals.amount_uses
import br.com.colman.petals.R.string.add_strain
import br.com.colman.petals.R.string.amount_grams
import br.com.colman.petals.R.string.archive_strain
import br.com.colman.petals.R.string.average_rating_out_of_five
import br.com.colman.petals.R.string.cost_per_gram
import br.com.colman.petals.R.string.delete_strain
import br.com.colman.petals.R.string.delete_strain_confirm
import br.com.colman.petals.R.string.edit_strain
import br.com.colman.petals.R.string.no
import br.com.colman.petals.R.string.no_strains_yet
import br.com.colman.petals.R.string.show_archived
import br.com.colman.petals.R.string.strains
import br.com.colman.petals.R.string.strains_description
import br.com.colman.petals.R.string.total_spent
import br.com.colman.petals.R.string.unarchive_strain
import br.com.colman.petals.R.string.yes
import br.com.colman.petals.components.StarRating
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.strain.repository.StrainRepository
import br.com.colman.petals.use.repository.Rating
import br.com.colman.petals.use.repository.UseRepository
import compose.icons.TablerIcons
import compose.icons.tablericons.Archive
import compose.icons.tablericons.ArrowBackUp
import compose.icons.tablericons.Leaf
import compose.icons.tablericons.Pencil
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Trash
import org.koin.compose.koinInject
import java.math.RoundingMode.HALF_UP
import java.util.Locale

/**
 * The strain catalog: adding and editing strains, archiving the ones the user is done with, and deleting a strain no
 * use was logged with, so a use never loses its strain. Editing a strain changes it for every use logged with it; a
 * new batch with another potency is a new strain.
 */
@Composable
fun StrainsPage(
  repository: StrainRepository = koinInject(),
  useRepository: UseRepository = koinInject(),
  settingsRepository: SettingsRepository = koinInject()
) {
  val catalog by remember(repository) { repository.all() }.collectAsState(null)
  // Null until the uses are read, so no strain is offered for deletion before its uses are known.
  val uses by remember(useRepository) { useRepository.all() }.collectAsState(null)
  val summaries = remember(uses) { uses?.summariesByStrain() }
  var isShowingArchived by remember { mutableStateOf(false) }
  var isAdding by remember { mutableStateOf(false) }
  var editing by remember { mutableStateOf<Strain?>(null) }
  val shown = catalog.orEmpty().filter { isShowingArchived || !it.isArchived }

  Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp), spacedBy(8.dp)) {
    Text(stringResource(strains), fontWeight = Bold, fontSize = 20.sp)

    Row(Modifier.fillMaxWidth(), spacedBy(8.dp), CenterVertically) {
      Button({ isAdding = true }) {
        Icon(TablerIcons.Plus, null)
        Text(stringResource(add_strain))
      }
      Spacer(Modifier.weight(1f))
      Text(stringResource(show_archived))
      Switch(isShowingArchived, { isShowingArchived = it }, Modifier.testTag("ShowArchivedStrains"))
    }

    if (catalog?.isEmpty() == true) {
      Text(stringResource(no_strains_yet))
    }

    shown.forEach { strain ->
      key(strain.id) {
        StrainCard(
          strain,
          summaries?.let { StrainUsage(it[strain.id]) },
          settingsRepository,
          onEdit = { editing = strain },
          onArchive = { repository.upsert(strain.copy(isArchived = !strain.isArchived)) },
          onDelete = { repository.delete(strain) }
        )
      }
    }
  }

  if (isAdding) {
    StrainDialog(catalog.orEmpty(), onDismiss = { isAdding = false }) {
      repository.upsert(it)
      isAdding = false
    }
  }

  editing?.let { strain ->
    StrainDialog(catalog.orEmpty(), strain, onDismiss = { editing = null }) {
      repository.upsert(it)
      editing = null
    }
  }
}

@Suppress("LongParameterList")
@Composable
private fun StrainCard(
  strain: Strain,
  usage: StrainUsage?,
  settingsRepository: SettingsRepository,
  onEdit: () -> Unit,
  onArchive: () -> Unit,
  onDelete: () -> Unit
) {
  val currencySymbol by settingsRepository.currencyIcon.collectAsState("$")
  val decimalPrecision by settingsRepository.decimalPrecision.collectAsState(settingsRepository.decimalPrecisionList[2])
  var isConfirmingDelete by remember { mutableStateOf(false) }

  Card(Modifier.fillMaxWidth().testTag("StrainCard ${strain.name}"), elevation = 6.dp) {
    Row(Modifier.padding(16.dp), spacedBy(16.dp), CenterVertically) {
      Column(Modifier.weight(1f), spacedBy(4.dp)) {
        Text(strain.displayName(), fontWeight = Bold)
        strain.potencyText()?.let { Text(it) }
        strain.costPerGram?.let {
          Text("$currencySymbol " + stringResource(cost_per_gram, it.setScale(decimalPrecision, HALF_UP).toString()))
        }
        usage?.let { UsageText(it.summary, currencySymbol, decimalPrecision) }
      }

      Column(horizontalAlignment = End) {
        IconButton(onEdit) { Icon(TablerIcons.Pencil, stringResource(edit_strain)) }
        if (strain.isArchived) {
          IconButton(onArchive) { Icon(TablerIcons.ArrowBackUp, stringResource(unarchive_strain)) }
        } else {
          IconButton(onArchive) { Icon(TablerIcons.Archive, stringResource(archive_strain)) }
        }
        if (usage != null && usage.summary == null) {
          IconButton({ isConfirmingDelete = true }) { Icon(TablerIcons.Trash, stringResource(delete_strain)) }
        }
      }
    }
  }

  if (isConfirmingDelete) {
    AlertDialog(
      onDismissRequest = { isConfirmingDelete = false },
      title = { Text(stringResource(delete_strain)) },
      text = { Text(stringResource(delete_strain_confirm, strain.name)) },
      confirmButton = {
        TextButton({
          onDelete()
          isConfirmingDelete = false
        }) { Text(stringResource(yes)) }
      },
      dismissButton = { TextButton({ isConfirmingDelete = false }) { Text(stringResource(no)) } }
    )
  }
}

/** What a strain's card says about its uses, once they are read; [summary] is null for a strain never used. */
private class StrainUsage(val summary: StrainSummary?)

/** "2 uses · 0.55 grams · $ 6.88 total", and the average rating in stars when any use was rated. */
@Composable
private fun UsageText(summary: StrainSummary?, currencySymbol: String, decimalPrecision: Int) {
  val uses = summary?.uses ?: 0
  val parts = listOfNotNull(
    pluralStringResource(amount_uses, uses, uses.toString()),
    summary?.let { stringResource(amount_grams, it.grams.setScale(decimalPrecision, HALF_UP).toPlainString()) },
    summary?.let {
      "$currencySymbol " + stringResource(total_spent, it.spent.setScale(decimalPrecision, HALF_UP).toPlainString())
    }
  )
  Text(parts.joinToString(" · "))

  summary?.averageRating?.let { average ->
    val description = stringResource(average_rating_out_of_five, "%.1f".format(Locale.ROOT, average).removeSuffix(".0"))
    StarRating(Rating.ofOrNull(average), starSize = 20.dp, description = description)
  }
}

/** The settings entry that opens [StrainsPage]. */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StrainsListItem(onClick: () -> Unit) {
  ListItem(
    modifier = Modifier.clickable(onClick = onClick).testTag("StrainsListItem"),
    icon = { Icon(TablerIcons.Leaf, null, Modifier.size(42.dp)) },
    secondaryText = { Text(stringResource(strains_description)) }
  ) {
    Text(stringResource(strains))
  }
}
