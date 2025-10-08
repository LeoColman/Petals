#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.5.0")

@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v5")
@file:DependsOn("gradle:actions__setup-gradle:v5")
@file:DependsOn("entrostat:git-secret-action:v4")
@file:DependsOn("ruby:setup-ruby:v1")
@file:DependsOn("softprops:action-gh-release:v2")

import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.entrostat.GitSecretAction
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.actions.ruby.SetupRuby
import io.github.typesafegithub.workflows.actions.softprops.ActionGhRelease
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
  job(id = "release", runsOn = UbuntuLatest) {
    run(
      name = "Setup Git Identity",
      command = """
            git config --global user.name "GitHub Actions"
            git config --global user.email "actions@github.com"
        """.trimIndent()
    )

    uses(name = "Setup JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(name = "Setup Gradle", action = ActionsSetupGradle())
    uses(name = "Checkout with SSHKey", action = Checkout(sshKey = expr { secrets["RELEASE_KEY"]!!}))
    uses(name = "Reveal Secrets", action = GitSecretAction(gpgPrivateKey = expr { GPG_KEY }))

    val versionTypeExpr = expr { github["event.inputs.version_type"]!! }
    val changelogExpr = expr { github["event.inputs.changelog"]!! }
    val bumpStep = run(
      name = "Run Bump Version Script",
      command = "app/bump_version.main.kts \"$versionTypeExpr\" \"$changelogExpr\"",
    )

    run(name = "Create APK", command = "./gradlew assembleGithubRelease")

    uses(
      name = "Create Github Release",
      action = ActionGhRelease(
        tagName = expr { bumpStep.outputs["version"] },
        name = expr { bumpStep.outputs["version"] },
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

    run(name = "Create Playstore Bundle", command = "./gradlew clean bundlePlaystoreRelease")

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
