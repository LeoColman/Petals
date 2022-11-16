#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.31.0")

import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.actions.UploadArtifactV3
import it.krzeminski.githubactions.actions.gradle.GradleBuildActionV2
import it.krzeminski.githubactions.domain.RunnerType
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile

workflow(
  name = "Dependency License Analysis",
  on = listOf(Push()),
  sourceFile = __FILE__.toPath()
) {
  job("analyse", runsOn = RunnerType.UbuntuLatest) {
    uses(CheckoutV3())
    uses(GradleBuildActionV2(arguments = "checkLicense"))
    run("cat build/reports/dependency-license/licenses.md >> \$GITHUB_STEP_SUMMARY")
  }
}.writeToFile()