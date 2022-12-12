package br.com.colman.petals.playstore

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import br.com.colman.petals.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdsView() {
  AndroidView({
    AdView(it).apply {
      setAdSize(AdSize.BANNER)
      adUnitId =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/6300978111" else "ca-app-pub-4066886200642192/3952973327"
      loadAd(AdRequest.Builder().build())
    }
  }, Modifier.fillMaxWidth())
}
