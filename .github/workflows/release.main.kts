#!/usr/bin/env kotlin
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:0.46.0")

import io.github.typesafegithub.workflows.actions.actions.CheckoutV3
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV3
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV3.Distribution.Adopt
import io.github.typesafegithub.workflows.actions.entrostat.GitSecretActionV4
import io.github.typesafegithub.workflows.actions.gradle.GradleBuildActionV2
import io.github.typesafegithub.workflows.actions.ruby.SetupRubyV1
import io.github.typesafegithub.workflows.actions.softprops.ActionGhReleaseV1
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.writeToFile


val GPG_KEY by Contexts.secrets
val GITHUB_REF_NAME by Contexts.github

workflow(
  name = "Release",
  on = listOf(Push(tags = listOf("*"))),
  sourceFile = __FILE__.toPath(),
) {
  job(id = "create-apk", runsOn = UbuntuLatest) {
    uses(name = "Set up JDK", action = SetupJavaV3(javaVersion = "17", distribution = Adopt))
    uses(action = CheckoutV3())
    uses(name = "reveal-secrets", action = GitSecretActionV4(gpgPrivateKey = expr { GPG_KEY }))

    uses(name = "Create APK", action = GradleBuildActionV2(arguments = "packageGithubReleaseUniversalApk"))

    uses(
      name = "Create release", action = ActionGhReleaseV1(
        tagName = expr { GITHUB_REF_NAME },
        name = "Version " + expr { GITHUB_REF_NAME },
        draft = true,
        files = listOf(
          "app/build/outputs/apk_from_bundle/githubRelease/app-github-release-universal.apk",
          "app/build/outputs/mapping/githubRelease/mapping.txt",
          "app/build/outputs/mapping/githubRelease/configuration.txt",
          "app/build/outputs/mapping/githubRelease/seeds.txt",
          "app/build/outputs/mapping/githubRelease/usage.txt",

          )
      )
    )

    uses(action = SetupRubyV1(rubyVersion = "2.6"))
    run(
      name = "publish-playstore",
      command = "bundle config path vendor/bundle && bundle install --jobs 4 --retry 3 && bundle exec fastlane playstore"
    )
  }
}.writeToFile()
