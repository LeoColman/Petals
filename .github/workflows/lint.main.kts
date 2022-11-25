#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.32.0")

import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.gradle.GradleBuildActionV2
import it.krzeminski.githubactions.domain.RunnerType.UbuntuLatest
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile

workflow(
  name = "Lint",
  on = listOf(Push()),
  sourceFile = __FILE__.toPath()
) {
  job("detekt", runsOn = UbuntuLatest) {
    uses(CheckoutV3())
    uses(GradleBuildActionV2(arguments = "detekt"))
  }
}.writeToFile()
