#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:Repository("https://github-workflows-kt-bindings.colman.com.br/binding/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:2.0.0")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("gradle:gradle-build-action:v3")
@file:DependsOn("reactivecircus:android-emulator-runner:v2")
import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.reactivecircus.AndroidEmulatorRunner
import io.github.typesafegithub.workflows.actions.reactivecircus.AndroidEmulatorRunner.Arch
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.workflow

workflow(
  name = "Android Tests",
  on = listOf(Push(), PullRequest()),
  sourceFile = __FILE__
) {
  job(id = "android-tests", runsOn = RunnerType.UbuntuLatest) {
    uses(name = "Set up JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(action = Checkout())
    uses(
      name = "Set up Android Emulator",
      action = AndroidEmulatorRunner(
        apiLevel = 29,
        arch = Arch.X86,
        script = "gradle connectedAndroidTest"
      )
    )
  }
}
