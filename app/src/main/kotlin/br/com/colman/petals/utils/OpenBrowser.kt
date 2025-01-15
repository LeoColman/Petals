package br.com.colman.petals.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun launchBrowser(url: String, context: Context) {
  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
  intent.resolveActivity(context.packageManager)?.let {
    context.startActivity(intent)
  }
}
