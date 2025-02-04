package br.com.colman.petals.use.io.output

import android.content.Context
import android.content.res.Resources
import br.com.colman.petals.use.repository.UseRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class UseOutputModuleTest : FunSpec({
  val koin = koinApplication {
    modules(
      UseOutputModule,
      module {
        single { mockk<UseRepository>() }
        single { mockk<Resources>(relaxed = true) }
        single {
          mockk<Context> {
            every { filesDir } returns tempdir()
          }
        }
      }
    )
  }.koin

  test("Should resolve UseCSVHeaders") {
    koin.get<UseCsvHeaders>() shouldNotBe null
  }

  test("Should resolve UseCsvSerializer") {
    koin.get<UseCsvSerializer>() shouldNotBe null
  }

  test("Should resolve FileWriter") {
    koin.get<FileWriter>() shouldNotBe null
  }

  test("Should resolve UseExporter") {
    koin.get<UseExporter>() shouldNotBe null
  }
})
