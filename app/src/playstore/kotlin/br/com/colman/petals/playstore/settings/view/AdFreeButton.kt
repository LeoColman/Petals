package br.com.colman.petals.playstore.settings.view

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R
import br.com.colman.petals.playstore.inapp.InAppPurchase
import org.koin.java.KoinJavaComponent.inject



@Preview
@Composable
fun AdFreeButton() {

  val inApp: InAppPurchase by inject(InAppPurchase::class.java)
  val activity:Activity = LocalContext.current as Activity
   Image(painter = painterResource(R.drawable.ic_ad_circle) ,
    contentDescription = "ads",
    colorFilter = ColorFilter.tint(color = LocalContentColor.current.copy(
      LocalContentAlpha.current
    ),
      blendMode = BlendMode.SrcIn),
    modifier = Modifier.size(42.dp).clickable{
      inApp.purchase(activity)
    })
}
