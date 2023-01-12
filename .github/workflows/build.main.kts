#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.34.2")

import it.krzeminski.githubactions.actions.CustomAction
import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.actions.SetupJavaV3
import it.krzeminski.githubactions.actions.gradle.GradleBuildActionV2
import it.krzeminski.githubactions.domain.RunnerType
import it.krzeminski.githubactions.domain.triggers.PullRequest
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.dsl.expressions.Contexts
import it.krzeminski.githubactions.dsl.expressions.expr
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile

val GPG_KEY by Contexts.secrets

workflow(
  name = "Build Universal APK",
  on = listOf(Push(), PullRequest()),
  sourceFile = __FILE__.toPath()
) {
  job("build", runsOn = RunnerType.UbuntuLatest) {
    uses(name = "Set up JDK", SetupJavaV3("11", distribution = SetupJavaV3.Distribution.Adopt))
    uses(CheckoutV3())
    uses(
      "reveal-secrets", CustomAction(
        "entrostat",
        "git-secret-action",
        "v3.3.0",
        mapOf("gpg-private-key" to expr { GPG_KEY })
      )
    )

    uses(
      "Create APK", GradleBuildActionV2(
        arguments = "packageGithubReleaseUniversalApk"
      )
    )
  }
}.writeToFile()