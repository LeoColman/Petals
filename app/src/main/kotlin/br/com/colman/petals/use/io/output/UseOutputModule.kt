package br.com.colman.petals.use.io.output

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val UseOutputModule = module {
  single { UseCsvHeaders(get()) }
  singleOf(::UseCsvSerializer)
  single { FileWriter(get()) }
  singleOf(::UseExporter)
}
