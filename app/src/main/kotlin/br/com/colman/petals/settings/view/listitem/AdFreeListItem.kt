package br.com.colman.petals.settings.view.listitem

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.InAppPurchaseUtil
import br.com.colman.petals.R
import compose.icons.TablerIcons
import compose.icons.tablericons.Crown
import org.koin.java.KoinJavaComponent.inject

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun AdFreeListItem() {
  val inApp: InAppPurchaseUtil by inject(InAppPurchaseUtil::class.java)
  val activity:Activity = LocalContext.current as Activity
  ListItem(
    modifier = Modifier.clickable {
      inApp.purchase(activity)
    },
    icon = { Icon(TablerIcons.Crown, null, Modifier.size(42.dp)) },
    secondaryText = { Text(stringResource(R.string.remove_ads)) }
  ) {
    Text(stringResource(R.string.remove_ads_title))
  }
}
