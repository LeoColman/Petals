package br.com.colman.petals.use.io

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val inputModule = module {
  singleOf(::UseImporter)
  singleOf(::UseCsvFileImporter)
}

private val exportModule = module {
  singleOf(::UseCsvHeadersFactory)
  singleOf(::UseCsvSerializer)
  singleOf(::FileWriter)
  singleOf(::UseExporter)
}

val IoModules = inputModule + exportModule
