@file:OptIn(ExperimentalMaterialApi::class)

package br.com.colman.petals.settings.view.listitem

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import br.com.colman.petals.R.string

@Preview
@Composable
fun ShareApp(
  shareIcon: ImageVector = Icons.Default.Share,
  context: Context = LocalContext.current
) {
  val sendIntent: Intent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(
      Intent.EXTRA_TEXT,
      stringResource(string.share_app_message)
    )
    type = "text/plain"
  }
  val shareIntent = Intent.createChooser(sendIntent, null)
  ListItem(
    modifier = Modifier.clickable {
      ContextCompat.startActivity(context, shareIntent, null)
    },
    icon = { Icon(shareIcon, null, Modifier.size(42.dp)) },
    secondaryText = { Text(stringResource(string.share_app)) }
  ) {
    Text(stringResource(string.share_app_title))
  }
}
