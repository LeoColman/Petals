package br.com.colman.petals.AppRating

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


fun incrementcounter(context:Context){
  val prefs= context.getSharedPreferences(Preferences.PrefsName,Context.MODE_PRIVATE)
  val currentcount= prefs.getInt(Preferences.Key_Uage_Count,0)
  prefs.edit().putInt(Preferences.Key_Uage_Count,currentcount+1).apply()
}

fun Shouldshowratingprompt(context: Context):Boolean{
  val prefs = context.getSharedPreferences(Preferences.PrefsName,Context.MODE_PRIVATE)
  val currentcount = prefs.getInt(Preferences.Key_Uage_Count,0)
  val dontaskagain = prefs.getBoolean(Preferences.Key_Dont_Ask_Again,false)
  return  currentcount >= Preferences.uage_Threshold && !dontaskagain
}


@Composable
fun RatingDialog(ondismiss:()-> Unit, onRateNow:()-> Unit, Ondontaskagain:()-> Unit){

  AlertDialog(
    onDismissRequest = ondismiss,
    title = { Text(text = "Rate our Petals") },
    text = { Text(text = "Please take some time to rate our app") },
    confirmButton = {
      TextButton(onClick = {
        onRateNow
        ondismiss
      }){
        Text(text = "Rate now")
      }
    },
    dismissButton = {
      TextButton(onClick = {
        Ondontaskagain
        ondismiss
      }){
        Text(text="Dont ask Again")
      }
    }
  )
}



@Composable
fun MainScreen(context: Context){
  val showdilaog = remember { mutableStateOf(false) }
  val prefs = context.getSharedPreferences(Preferences.PrefsName,Context.MODE_PRIVATE)
  LaunchedEffect(Unit){
    if(Shouldshowratingprompt(context)){
      showdilaog.value= true
    }
    incrementcounter(context)
  }

  if(showdilaog.value){
    RatingDialog(
      ondismiss = {
        showdilaog.value =false
      },
      onRateNow = {
        val intent=Intent(Intent.ACTION_VIEW).apply {
          data = Uri.parse("market://details?id=${context.packageName}")
          setPackage("com.android.vending")
        }
        context.startActivity(intent)
        showdilaog.value = false
        prefs.edit().putInt(Preferences.Key_Uage_Count,0).apply()
      },
      Ondontaskagain = {
        prefs.edit().putBoolean(Preferences.Key_Dont_Ask_Again,true).apply()
      }
    )
  }

}
