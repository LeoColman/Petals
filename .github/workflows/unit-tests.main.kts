#!/usr/bin/env kotlin
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:1.0.0")

import io.github.typesafegithub.workflows.actions.actions.CheckoutV3
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV3
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV3.Distribution.Adopt
import io.github.typesafegithub.workflows.actions.gradle.GradleBuildActionV2
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.writeToFile


workflow(
  name = "Unit Tests",
  on = listOf(Push(), PullRequest()),
  sourceFile = __FILE__.toPath()
) {
  job(id = "unit-test", runsOn = RunnerType.UbuntuLatest) {
    uses(name = "Set up JDK", action = SetupJavaV3(javaVersion = "17", distribution = Adopt))
    uses(action = CheckoutV3())
    uses(action = GradleBuildActionV2(arguments = "test"))
  }
}.writeToFile()