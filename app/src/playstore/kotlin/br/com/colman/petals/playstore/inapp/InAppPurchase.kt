package br.com.colman.petals.playstore.inapp

import android.app.Activity
import android.content.Context
import br.com.colman.petals.BuildConfig
import br.com.colman.petals.playstore.settings.AdsSettingsRepository
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class InAppPurchase(val context: Context, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
  PurchasesUpdatedListener {
  private val myBilled: BillingClient = BillingClient.newBuilder(context)
    .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
    .setListener(this)
    .build()

  private val productId = if (BuildConfig.DEBUG) "android.test.purchased" else "petals_remove_ads"
  private val settingsRepository: AdsSettingsRepository by inject(AdsSettingsRepository::class.java)
  private var lstProductDetails: List<ProductDetails>? = null

  init {
    myBilled.startConnection(object : BillingClientStateListener {
      override fun onBillingServiceDisconnected() = Unit

      override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode != BillingResponseCode.OK) return

        myBilled.queryProductDetailsAsync(
          QueryProductDetailsParams.newBuilder().setProductList(
            listOf(
              QueryProductDetailsParams.Product.newBuilder().setProductId(productId)
                .setProductType(ProductType.INAPP).build()
            )
          ).build()
        ) { _, productDetails ->
          lstProductDetails = productDetails.productDetailsList
        }

        restorePurchases()
      }
    })
  }

  /**
   * Play, not the device, owns the answer to whether the ads were bought away. The local flag is
   * wiped by a reinstall or is simply missing on a new device, so ask Play on every start and put
   * the flag back where it belongs.
   */
  private fun restorePurchases() {
    myBilled.queryPurchasesAsync(
      QueryPurchasesParams.newBuilder().setProductType(ProductType.INAPP).build()
    ) { billingResult, purchases ->
      if (billingResult.responseCode != BillingResponseCode.OK) return@queryPurchasesAsync

      val adFreePurchases = purchases.filter { it.isAdFree }
      adFreePurchases.forEach(::acknowledge)
      settingsRepository.setAdFree(adFreePurchases.isNotEmpty())
    }
  }

  fun purchase(activity: Activity) {
    lstProductDetails?.let {
      if (it.isNotEmpty()) {
        myBilled.launchBillingFlow(
          activity,
          BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
              listOf(
                ProductDetailsParams.newBuilder().setProductDetails(
                  it[0]
                ).build()
              )
            ).build()
        )
      }
    }
  }

  override fun onPurchasesUpdated(p0: BillingResult, purchase: MutableList<Purchase>?) {
    // Someone who already owns it lands here when they tap buy again, and so does anyone who backs
    // out of the Play dialog. Neither says anything about the entitlement, so ask Play instead of
    // reading the empty list as "not bought".
    if (p0.responseCode != BillingResponseCode.OK || purchase.isNullOrEmpty()) {
      restorePurchases()
      return
    }

    purchase.filter { it.isAdFree }.forEach {
      acknowledge(it)
      settingsRepository.setAdFree(true)
    }
  }

  private fun acknowledge(purchase: Purchase) {
    if (purchase.isAcknowledged) return

    CoroutineScope(dispatcher).launch {
      myBilled.acknowledgePurchase(
        AcknowledgePurchaseParams.newBuilder().setPurchaseToken(
          purchase.purchaseToken
        ).build()
      ) {
      }
    }
  }

  private val Purchase.isAdFree
    get() = productId in products && purchaseState == Purchase.PurchaseState.PURCHASED
}
