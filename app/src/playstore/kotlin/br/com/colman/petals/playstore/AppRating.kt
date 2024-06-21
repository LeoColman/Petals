package br.com.colman.petals.AppRating

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import br.com.colman.petals.utils.Preferences


fun incrementCounter(context: Context) {
  val prefs = context.getSharedPreferences(Preferences.PrefsName, Context.MODE_PRIVATE)
  val currentCount = prefs.getInt(Preferences.Key_Uage_Count, 0)
  prefs.edit().putInt(Preferences.Key_Uage_Count, currentCount + 1).apply()
}

fun shouldShowRatingPrompt(context: Context): Boolean {
  val prefs = context.getSharedPreferences(Preferences.PrefsName, Context.MODE_PRIVATE)
  val currentCount = prefs.getInt(Preferences.Key_Uage_Count, 0)
  val dontAskAgain = prefs.getBoolean(Preferences.Key_Dont_Ask_Again, false)
  return currentCount >= Preferences.uage_Threshold && !dontAskAgain
}

@Composable
fun RatingDialog(onDismiss: () -> Unit, onRateNow: () -> Unit, onDontAskAgain: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = "Rate our Petals") },
    text = { Text(text = "Please take some time to rate our app") },
    confirmButton = {
      TextButton(onClick = {
        onRateNow()
        onDismiss()
      }) {
        Text(text = "Rate now")
      }
    },
    dismissButton = {
      TextButton(onClick = {
        onDontAskAgain()
        onDismiss()
      }) {
        Text(text = "Don't ask again")
      }
    }
  )
}

@Composable
fun MainScreen(context: Context) {
  val showDialog = remember { mutableStateOf(false) }
  val prefs = context.getSharedPreferences(Preferences.PrefsName, Context.MODE_PRIVATE)

  LaunchedEffect(Unit) {
    if (shouldShowRatingPrompt(context)) {
      showDialog.value = true
    }
    incrementCounter(context)
  }

  if (showDialog.value) {
    RatingDialog(
      onDismiss = { showDialog.value = false },
      onRateNow = {
        val intent = Intent(Intent.ACTION_VIEW).apply {
          data = Uri.parse("market://details?id=${context.packageName}")
          setPackage("com.android.vending")
        }
        context.startActivity(intent)
        showDialog.value = false
        prefs.edit().putInt(Preferences.Key_Uage_Count, 0).apply()
      },
      onDontAskAgain = {
        prefs.edit().putBoolean(Preferences.Key_Dont_Ask_Again, true).apply()
      }
    )
  }
}
