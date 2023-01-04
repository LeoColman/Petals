#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.34.1")

import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.gradle.GradleBuildActionV2
import it.krzeminski.githubactions.domain.RunnerType
import it.krzeminski.githubactions.domain.triggers.PullRequest
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile


workflow(
  name = "Unit Tests",
  on = listOf(Push(), PullRequest()),
  sourceFile = __FILE__.toPath()
) {
  job("unit-test", runsOn = RunnerType.UbuntuLatest) {
    uses(CheckoutV3())
    uses(GradleBuildActionV2(arguments = "test"))
  }
}.writeToFile()