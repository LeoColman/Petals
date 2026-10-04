package br.com.colman.petals.use.io.input

import br.com.colman.petals.Database
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val UseInputModule = module {
  single { UseImporter(get(), get(), get<Database>()) }
  singleOf(::UseCsvFileImporter)
}
