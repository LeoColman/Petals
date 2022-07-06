#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.21.0")

import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.actions.SetupJavaV3
import it.krzeminski.githubactions.actions.gradle.GradleBuildActionV2
import it.krzeminski.githubactions.actions.softprops.ActionGhReleaseV1
import it.krzeminski.githubactions.domain.RunnerType.UbuntuLatest
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.dsl.expressions.Contexts
import it.krzeminski.githubactions.dsl.expressions.expr
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile

val KEYSTORE_FILE_BASE64 by Contexts.secrets
val KEYSTORE_PASSWORD by Contexts.secrets
val SIGNING_KEY_ALIAS by Contexts.secrets
val SIGNING_KEY_PASSWORD by Contexts.secrets
val TAG = Contexts.github.ref

workflow(
  name = "Release",
  on = listOf(Push(tags = listOf("*"))),
  sourceFile = __FILE__.toPath(),
  env = linkedMapOf(
    "KEYSTORE_FILE_BASE64" to expr { KEYSTORE_FILE_BASE64 },
    "KEYSTORE_PASSWORD" to expr { KEYSTORE_PASSWORD },
    "SIGNING_KEY_ALIAS" to expr { SIGNING_KEY_ALIAS },
    "SIGNING_KEY_PASSWORD" to expr { SIGNING_KEY_PASSWORD }
  )
) {
  job("create-apk", runsOn = UbuntuLatest, env = linkedMapOf("KEYSTORE_FILE" to "../local/keystore")) {
    uses(name = "Set up JDK", SetupJavaV3("11", SetupJavaV3.Distribution.Adopt))
    uses(CheckoutV3())
    run("mkdir local")
    run("echo \$KEYSTORE_FILE_BASE64 | base64 --decode >> local/keystore")
    uses("Create APK", GradleBuildActionV2(
      arguments = "packageGithubReleaseUniversalApk"
    ))

    uses("Create release", ActionGhReleaseV1(
      tagName = expr { TAG },
      name = "Version " + expr { TAG },
      draft = true,
      files = listOf("app/build/outputs/universal_apk/githubRelease/app-github-release-universal.apk")
    ))
  }
}.writeToFile()
