package br.com.colman.petals.use.io.input

import android.content.ContentResolver
import br.com.colman.petals.use.repository.UseRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class UseInputModuleTest : FunSpec({

  val koin = koinApplication {
    modules(
      UseInputModule,
      module {
        single { mockk<UseRepository>() }
        single { mockk<ContentResolver>() }
      }
    )
  }.koin

  test("Should resolve an UseCsvFileImporter") {
    koin.get<UseCsvFileImporter>() shouldNotBe null
  }

  test("Should resolve an UseImporter") {
    koin.get<UseImporter>() shouldNotBe null
  }
})
