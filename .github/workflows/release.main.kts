#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:2.0.0")

@file:Repository("https://github-workflows-kt-bindings.colman.com.br/binding/")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("gradle:gradle-build-action:v3")
@file:DependsOn("entrostat:git-secret-action:v4")
@file:DependsOn("ruby:setup-ruby:v1")
@file:DependsOn("softprops:action-gh-release:v2")

import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.entrostat.GitSecretAction
import io.github.typesafegithub.workflows.actions.gradle.GradleBuildAction
import io.github.typesafegithub.workflows.actions.ruby.SetupRuby
import io.github.typesafegithub.workflows.actions.softprops.ActionGhRelease
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
    uses(name = "Set up JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(action = Checkout())
    uses(name = "reveal-secrets", action = GitSecretAction(gpgPrivateKey = expr { GPG_KEY }))

    uses(name = "Create APK", action = GradleBuildAction(arguments = "packageGithubReleaseUniversalApk"))

    uses(
      name = "Create release", action = ActionGhRelease(
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

    uses(action = SetupRuby(rubyVersion = "2.6"))
    run(
      name = "publish-playstore",
      command = "bundle config path vendor/bundle && bundle install --jobs 4 --retry 3 && bundle exec fastlane playstore"
    )
  }
}.writeToFile()
