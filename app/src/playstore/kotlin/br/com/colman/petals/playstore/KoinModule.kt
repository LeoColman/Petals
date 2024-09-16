package br.com.colman.petals.playstore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import br.com.colman.petals.playstore.inapp.InAppPurchase
import br.com.colman.petals.playstore.settings.AdsSettingsRepository
import org.koin.dsl.module

private val Context.adsSettings by preferencesDataStore("ads_settings")

val KoinModule = module {
  single {
    InAppPurchase(get<Context>())
  }
  single {
    AdsSettingsRepository(get<Context>().adsSettings)
  }
}


