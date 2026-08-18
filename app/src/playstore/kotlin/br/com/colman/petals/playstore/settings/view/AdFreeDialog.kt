package br.com.colman.petals.playstore.settings.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string
import java.time.Duration

@Preview
@Composable
fun AdFreeDialog(
  timeRemaining: Duration? = null,
  onWatchAd: () -> Unit = {},
  onPurchase: () -> Unit = {},
  onDismiss: () -> Unit = {}
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(stringResource(string.remove_ads_title), style = MaterialTheme.typography.h5)
    },
    text = {
      Column {
        Text(stringResource(string.watch_ad_description))
        if (timeRemaining != null) {
          Spacer(Modifier.height(8.dp))
          Text(
            stringResource(string.ad_free_time_remaining, timeRemaining.toHours(), timeRemaining.toMinutes() % 60),
            style = MaterialTheme.typography.subtitle1
          )
        }
        Spacer(Modifier.height(16.dp))
        WatchAdButton(onWatchAd, onDismiss)
        Spacer(Modifier.height(8.dp))
        RemoveAdsForeverButton(onPurchase, onDismiss)
      }
    },
    confirmButton = {
      TextButton(onDismiss) { Text(stringResource(string.cancel)) }
    }
  )
}

@Composable
private fun WatchAdButton(onWatchAd: () -> Unit, onDismiss: () -> Unit) {
  Button(
    onClick = {
      onWatchAd()
      onDismiss()
    },
    modifier = Modifier.fillMaxWidth()
  ) {
    Text(stringResource(string.watch_ad))
  }
}

@Composable
private fun RemoveAdsForeverButton(onPurchase: () -> Unit, onDismiss: () -> Unit) {
  OutlinedButton(
    onClick = {
      onPurchase()
      onDismiss()
    },
    modifier = Modifier.fillMaxWidth()
  ) {
    Text(stringResource(string.remove_ads_forever))
  }
}
