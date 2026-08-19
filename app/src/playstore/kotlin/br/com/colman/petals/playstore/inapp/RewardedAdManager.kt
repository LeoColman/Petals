package br.com.colman.petals.playstore.inapp

import android.app.Activity
import android.content.Context
import br.com.colman.petals.BuildConfig
import br.com.colman.petals.playstore.settings.AdsSettingsRepository
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * Optional rewarded video: watching one hides the banner for a day. Nothing in the app sits behind
 * it, so the whole app stays free for whoever would rather not watch anything.
 */
class RewardedAdManager(
  private val context: Context,
  private val adsSettingsRepository: AdsSettingsRepository
) {

  private val adUnitId =
    if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/5224354917" else "ca-app-pub-9745951044027822/8007591291"

  private var loadedAd: RewardedAd? = null
  private var isLoading = false

  fun preload() {
    if (isLoading || loadedAd != null) return
    isLoading = true

    RewardedAd.load(
      context,
      adUnitId,
      AdRequest.Builder().build(),
      object : RewardedAdLoadCallback() {
        override fun onAdLoaded(ad: RewardedAd) {
          loadedAd = ad
          isLoading = false
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
          loadedAd = null
          isLoading = false
        }
      }
    )
  }

  /**
   * Shows the video when one is loaded, granting the ad-free time once it is watched through.
   * Calls [onUnavailable] and starts loading one instead when nothing is ready yet.
   */
  fun show(activity: Activity, onUnavailable: () -> Unit = {}) {
    val ad = loadedAd
    if (ad == null) {
      preload()
      onUnavailable()
      return
    }

    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
      override fun onAdDismissedFullScreenContent() = discardAndPreload()
      override fun onAdFailedToShowFullScreenContent(error: AdError) = discardAndPreload()
    }

    ad.show(activity) { adsSettingsRepository.grantRewardedAdFreeTime() }
  }

  private fun discardAndPreload() {
    loadedAd = null
    preload()
  }
}
