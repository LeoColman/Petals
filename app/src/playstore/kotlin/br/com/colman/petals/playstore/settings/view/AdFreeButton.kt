package br.com.colman.petals.playstore.settings.view

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R
import br.com.colman.petals.playstore.inapp.InAppPurchase
import br.com.colman.petals.playstore.inapp.RewardedAdManager
import br.com.colman.petals.playstore.settings.AdsSettingsRepository
import org.koin.compose.koinInject

@Preview
@Composable
fun AdFreeButton() {
  val inApp = koinInject<InAppPurchase>()
  val rewardedAdManager = koinInject<RewardedAdManager>()
  val adsSettingsRepository = koinInject<AdsSettingsRepository>()
  val context = LocalContext.current
  val activity: Activity = context as Activity
  val isAdFree by adsSettingsRepository.isAdsFree.collectAsState(initial = false)
  val adUnavailableMessage = stringResource(R.string.ad_not_available_yet)
  var isDialogShown by remember { mutableStateOf(false) }

  if (isAdFree) return

  LaunchedEffect(Unit) { rewardedAdManager.preload() }

  Image(
    painter = painterResource(R.drawable.ic_ad_circle),
    contentDescription = "ads",
    colorFilter = ColorFilter.tint(
      color = LocalContentColor.current.copy(
        LocalContentAlpha.current
      ),
      blendMode = BlendMode.SrcIn
    ),
    modifier = Modifier.size(42.dp).clickable { isDialogShown = true }
  )

  if (isDialogShown) {
    val timeRemaining by adsSettingsRepository.rewardedTimeRemaining.collectAsState(initial = null)

    AdFreeDialog(
      timeRemaining = timeRemaining,
      onWatchAd = {
        rewardedAdManager.show(activity) {
          Toast.makeText(context, adUnavailableMessage, Toast.LENGTH_SHORT).show()
        }
      },
      onPurchase = { inApp.purchase(activity) },
      onDismiss = { isDialogShown = false }
    )
  }
}
