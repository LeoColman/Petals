#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.2.0")

@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("gradle:gradle-build-action:v3")


import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.gradle.GradleBuildAction
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.workflow


workflow(
  name = "Build Universal Debug APK",
  on = listOf(Push(branches = listOf("main")), PullRequest()),
  sourceFile = __FILE__
) {
  job(id = "build", runsOn = RunnerType.UbuntuLatest) {
    uses(name = "Set up JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(action = Checkout())
    uses(name = "Create Fdroid APK", action = GradleBuildAction(arguments = "assembleFdroidDebug"))
    uses(name = "Create Playstore APK", action = GradleBuildAction(arguments = "assemblePlaystoreDebug"))
    uses(name = "Create Github APK", action = GradleBuildAction(arguments = "assembleGithubDebug"))
  }
}
