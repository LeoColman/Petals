package br.com.colman.petals.playstore.review

import android.app.Activity
import android.content.Context
import br.com.colman.petals.review.ReviewAppRequester
import com.google.android.play.core.review.ReviewManagerFactory
import timber.log.Timber

class ReviewAppPlaystoreRequester(context: Context) : ReviewAppRequester {

  private val reviewManager = ReviewManagerFactory.create(context)

  override fun requestReview(activity: Activity) {
    Timber.d("Requesting review")
    reviewManager.requestReviewFlow().addOnSuccessListener { reviewManager.launchReviewFlow(activity, it) }
  }
}
