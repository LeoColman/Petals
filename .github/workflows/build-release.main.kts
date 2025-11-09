#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.6.0")

@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v5")
@file:DependsOn("gradle:actions__setup-gradle:v4")
@file:DependsOn("entrostat:git-secret-action:v4")

import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.entrostat.GitSecretAction
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow

private val GPG_KEY by Contexts.secrets

workflow(
  name = "Build Universal Release APK",
  on = listOf(Push(branches = listOf("main"))),
  sourceFile = __FILE__
) {
  job(
    id = "build-release",
    runsOn = RunnerType.UbuntuLatest,
    strategyMatrix = mapOf("variant" to listOf("Fdroid", "Playstore", "Github"))
  ) {
    val variant = expr { "matrix.variant" }

    uses(name = "Setup JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(name = "Checkout", action = Checkout())
    uses(name = "Reveal Secrets", action = GitSecretAction(gpgPrivateKey = expr { GPG_KEY }))

    uses(name = "Setup Gradle", action = ActionsSetupGradle())
    run(name = "Create $variant APK", command = "./gradlew assemble${variant}Release")
  }
}
