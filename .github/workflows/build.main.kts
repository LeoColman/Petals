#!/usr/bin/env kotlin
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:0.47.0")

import io.github.typesafegithub.workflows.actions.actions.CheckoutV3
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV3
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV3.Distribution.Adopt
import io.github.typesafegithub.workflows.actions.gradle.GradleBuildActionV2
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.writeToFile


val GPG_KEY by Contexts.secrets

workflow(
  name = "Build Universal APK",
  on = listOf(Push(), PullRequest()),
  sourceFile = __FILE__.toPath()
) {
  job(id = "build", runsOn = RunnerType.UbuntuLatest) {
    uses(name = "Set up JDK", action = SetupJavaV3(javaVersion = "17", distribution = Adopt))
    uses(action = CheckoutV3())
    uses(name = "Create APK", action = GradleBuildActionV2(arguments = "packageFdroidReleaseUniversalApk"))
  }
}.writeToFile()