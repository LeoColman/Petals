#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:2.3.0")

@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("gradle:gradle-build-action:v3")
@file:DependsOn("entrostat:git-secret-action:v4")


import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.entrostat.GitSecretAction
import io.github.typesafegithub.workflows.actions.gradle.GradleBuildAction
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow

val GPG_KEY by Contexts.secrets


workflow(
  name = "Build Universal Release APK",
  on = listOf(Push(branches = listOf("main"))),
  sourceFile = __FILE__
) {
  job(id = "build", runsOn = RunnerType.UbuntuLatest) {
    uses(name = "Set up JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(action = Checkout())
    uses(name = "reveal-secrets", action = GitSecretAction(gpgPrivateKey = expr { GPG_KEY }))
    uses(name = "Create Fdroid APK", action = GradleBuildAction(arguments = "assembleFdroidRelease"))
    uses(name = "Create Playstore APK", action = GradleBuildAction(arguments = "assemblePlaystoreRelease"))
    uses(name = "Create Github APK", action = GradleBuildAction(arguments = "assembleGithubRelease"))
  }
}
