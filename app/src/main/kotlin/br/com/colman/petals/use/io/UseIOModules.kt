package br.com.colman.petals.use.io

import br.com.colman.petals.use.io.input.UseInputModule
import br.com.colman.petals.use.io.output.UseOutputModule
import br.com.colman.petals.use.io.output.auto.AutoExportModule

val UseIOModules = UseInputModule + UseOutputModule + AutoExportModule
