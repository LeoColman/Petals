package br.com.colman.petals.use.io.output.auto

import android.content.Context
import androidx.work.WorkManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val AutoExportModule = module {
  single { WorkManager.getInstance(get<Context>()) }
  single { AutoExportDocumentWriter(get()) }
  singleOf(::AutoExportScheduler)
  single { AutoExporter(get(), get(), get()) }
  single { AutoExportEnabler(get<Context>(), get(), get()) }
}
