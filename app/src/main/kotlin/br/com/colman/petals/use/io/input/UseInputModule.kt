package br.com.colman.petals.use.io.input

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val UseInputModule = module {
  singleOf(::UseImporter)
  singleOf(::UseCsvFileImporter)
}
