package br.com.colman.petals

import org.junit.Test
import org.koin.test.check.checkModules

class PetalsApplicationTest {
  @Test
  fun koinModules() {
    koin.checkModules()
  }
}
