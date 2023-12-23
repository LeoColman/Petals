#!/usr/bin/env kotlin
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:1.8.0")
@file:Import("generated/actions/checkout.kt")
@file:Import("generated/actions/setup-java.kt")
@file:Import("generated/gradle/gradle-build-action.kt")

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
    uses(name = "Set up JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(action = Checkout())
    uses(action = GradleBuildAction(arguments = "test"))
  }
}.writeToFile(generateActionBindings = true)
