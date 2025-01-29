package br.com.colman.petals.use.io

import br.com.colman.petals.use.io.input.UseInputModule
import br.com.colman.petals.use.io.output.UseOutputModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UseIOModulesTest : FunSpec({

  test("UseIOModules should be a list of just the InputModule and the OutputModule") {
    UseIOModules shouldBe listOf(UseInputModule, UseOutputModule)
  }
})
