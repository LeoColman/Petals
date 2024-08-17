package br.com.colman.petals.settings.view.listitem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string
import compose.icons.TablerIcons
import compose.icons.tablericons.BrandGithub

@Preview
@Composable
fun RepositoryListItem() {
  val uriHandler = LocalUriHandler.current
  val openProjectPage = { uriHandler.openUri("https://github.com/LeoColman/Petals") }

  ListItem(
    modifier = Modifier.clickable { openProjectPage() },
    leadingContent = { Icon(TablerIcons.BrandGithub, null, Modifier.size(42.dp)) },
    supportingContent = { Text(stringResource(string.repository_link_description)) },
    headlineContent = { Text(stringResource(string.repository_link_title)) }
  )
}
