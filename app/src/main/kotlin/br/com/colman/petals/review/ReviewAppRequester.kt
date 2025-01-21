package br.com.colman.petals.review

import android.app.Activity
import timber.log.Timber

interface ReviewAppRequester {
  fun requestReview(activity: Activity) {
    Timber.d("Default RequestReview Behaviour (No-Op)")
  }
}
