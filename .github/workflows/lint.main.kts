#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.39.0")

import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.gradle.GradleBuildActionV2
import it.krzeminski.githubactions.domain.RunnerType.UbuntuLatest
import it.krzeminski.githubactions.domain.triggers.PullRequest
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile

workflow(
  name = "Lint",
  on = listOf(Push(), PullRequest()),
  sourceFile = __FILE__.toPath()
) {
  job("detekt", runsOn = UbuntuLatest) {
    uses(CheckoutV3())
    uses(GradleBuildActionV2(arguments = "detekt"))
  }
}.writeToFile()
