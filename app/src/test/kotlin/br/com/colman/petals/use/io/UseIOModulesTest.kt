package br.com.colman.petals.use.io

import br.com.colman.petals.use.io.input.UseInputModule
import br.com.colman.petals.use.io.output.UseOutputModule
import br.com.colman.petals.use.io.output.auto.AutoExportModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UseIOModulesTest : FunSpec({

  test("UseIOModules should be a list of just the InputModule, OutputModule and AutoExportModule") {
    UseIOModules shouldBe listOf(UseInputModule, UseOutputModule, AutoExportModule)
  }
})
