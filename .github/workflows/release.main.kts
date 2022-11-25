#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.32.0")

import it.krzeminski.githubactions.actions.CustomAction
import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.actions.SetupJavaV3
import it.krzeminski.githubactions.actions.gradle.GradleBuildActionV2
import it.krzeminski.githubactions.actions.ruby.SetupRubyV1
import it.krzeminski.githubactions.actions.softprops.ActionGhReleaseV1
import it.krzeminski.githubactions.domain.RunnerType.UbuntuLatest
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.dsl.expressions.Contexts
import it.krzeminski.githubactions.dsl.expressions.expr
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile

val GPG_KEY by Contexts.secrets
val GITHUB_REF_NAME by Contexts.github

workflow(
  name = "Release",
  on = listOf(Push(tags = listOf("*"))),
  sourceFile = __FILE__.toPath(),
) {
  job("create-apk", runsOn = UbuntuLatest) {
    uses(name = "Set up JDK", SetupJavaV3("11", SetupJavaV3.Distribution.Adopt))
    uses(CheckoutV3())
    uses("reveal-secrets", CustomAction(
      "entrostat",
      "git-secret-action",
      "v3.3.0",
      mapOf("gpg-private-key" to expr { GPG_KEY })
    ))

    uses("Create APK", GradleBuildActionV2(
      arguments = "packageGithubReleaseUniversalApk"
    ))

    uses("Create release", ActionGhReleaseV1(
      tagName = expr { GITHUB_REF_NAME },
      name = "Version " + expr { GITHUB_REF_NAME },
      draft = true,
      files = listOf("app/build/outputs/universal_apk/githubRelease/app-github-release-universal.apk")
    ))

    uses(SetupRubyV1("2.6"))
    run("publish-playstore", "bundle config path vendor/bundle && bundle install --jobs 4 --retry 3 && bundle exec fastlane playstore")
  }
}.writeToFile()
