#!/usr/bin/env kotlin
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:1.6.0")
@file:Import("generated/actions/checkout.kt")
@file:Import("generated/actions/setup-java.kt")
@file:Import("generated/gradle/gradle-build-action.kt")
@file:Import("generated/entrostat/git-secret-action.kt")
@file:Import("generated/ruby/setup-ruby.kt")
@file:Import("generated/softprops/action-gh-release.kt")


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
}.writeToFile(generateActionBindings = true)
