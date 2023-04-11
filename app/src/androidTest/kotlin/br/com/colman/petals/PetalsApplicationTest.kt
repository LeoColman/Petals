package br.com.colman.petals

import br.com.colman.kotest.FunSpec
import org.koin.test.check.checkModules

class PetalsApplicationTest : FunSpec({
  test("Koin can resolve all modules") {
    koin.checkModules()
  }
})
