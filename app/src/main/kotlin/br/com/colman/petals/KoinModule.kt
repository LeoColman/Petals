package br.com.colman.petals

import br.com.colman.petals.clock.QuitTimer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val KoinModule = module {
   single { QuitTimer(androidContext()) }
}