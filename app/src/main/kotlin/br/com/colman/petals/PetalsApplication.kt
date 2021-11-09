package br.com.colman.petals

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PetalsApplication : Application() {
   override fun onCreate() {
      super.onCreate()
      startKoin {
         androidContext(this@PetalsApplication)
         modules(KoinModule)
      }
   }
}