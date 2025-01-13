#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.1.0")

@file:Repository("https://bindings.krzeminski.it")
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
import io.github.typesafegithub.workflows.domain.Mode.Write
import io.github.typesafegithub.workflows.domain.Permission.Contents
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.triggers.WorkflowDispatch
import io.github.typesafegithub.workflows.domain.triggers.WorkflowDispatch.Input
import io.github.typesafegithub.workflows.domain.triggers.WorkflowDispatch.Type.String
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow


val GPG_KEY by Contexts.secrets
val GITHUB_REF_NAME by Contexts.github

workflow(
  name = "Release",
  on = listOf(
    WorkflowDispatch(
      inputs = mapOf(
        "version_type" to Input(
          description = "Type of version bump (major, minor, patch)",
          required = true,
          type = WorkflowDispatch.Type.Choice,
          options = listOf("major", "minor", "patch")
        ),
        "changelog" to Input(
          description = "Changelog content for this release",
          required = true,
          type = String,
        )
      )
    )
  ),
  sourceFile = __FILE__
) {
  job(id = "create-apk", runsOn = UbuntuLatest, permissions = mapOf(Contents to Write)) {
    run(
      name = "Set up Git Identity",
      command = """
            git config --global user.name "GitHub Actions"
            git config --global user.email "actions@github.com"
        """.trimIndent()
    )

    uses(name = "Set up JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(action = Checkout())
    uses(name = "reveal-secrets", action = GitSecretAction(gpgPrivateKey = expr { GPG_KEY }))

    val versionTypeExpr = expr { github["event.inputs.version_type"]!! }
    val changelogExpr = expr { github["event.inputs.changelog"]!! }
    run(
      name = "Run Bump Version Script",
      command = "app/bump_version.main.kts $versionTypeExpr $changelogExpr",

    )


    uses(name = "Create APK", action = GradleBuildAction(arguments = "assembleGithubRelease"))

    uses(
      name = "Create release", action = ActionGhRelease(
        tagName = expr { GITHUB_REF_NAME },
        name = expr { GITHUB_REF_NAME },
        draft = false,
        files = listOf(
          "app/build/outputs/apk/github/release/app-github-release.apk",
          "app/build/outputs/mapping/githubRelease/mapping.txt",
          "app/build/outputs/mapping/githubRelease/configuration.txt",
          "app/build/outputs/mapping/githubRelease/seeds.txt",
          "app/build/outputs/mapping/githubRelease/usage.txt",

          )
      )
    )

    uses(name = "Create Bundle", action = GradleBuildAction(arguments = "clean bundlePlaystoreRelease"))

    uses(action = SetupRuby(rubyVersion = "3.2.3"))
    run(
      name = "Publish to Playstore",
      workingDirectory = "fastlane",
      command = """
          bundle config path vendor/bundle &&
          bundle install --jobs 4 --retry 3 &&
          bundle exec fastlane playstore
        """.trimIndent()
    )
  }
}
