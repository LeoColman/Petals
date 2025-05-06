#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:CompilerOptions("-Xmulti-dollar-interpolation")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.4.0")

@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("actions:upload-artifact:v4")
@file:DependsOn("gradle:actions__setup-gradle:v4")
@file:DependsOn("peaceiris:actions-gh-pages:v4")


import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.actions.peaceiris.ActionsGhPages
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow


workflow(
  name = "Kover Coverage",
  on = listOf(Push(branches = listOf("main"))),
  sourceFile = __FILE__
) {
  job(id = "kover-coverage", runsOn = RunnerType.UbuntuLatest) {
    uses(name = "Setup JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(name = "Checkout", action = Checkout())
    uses(name = "Setup Gradle", action = ActionsSetupGradle())

    run(name = "Generate Kover Reports", command = "./gradlew koverHtmlReport koverXmlReport")
    uses(
      name = "Upload Kover Reports",
      action = UploadArtifact(name = "coverage-reports", path = listOf("app/build/reports/kover"))
    )
    run(
      name = "Calculate Coverage",
      command = $$"""
                COVERAGE=$(./gradlew -q printLineCoverage)
                echo "Raw Coverage: $COVERAGE"
                COVERAGE=$(printf "%.0f" "$COVERAGE")  # Round to integer
                echo "Rounded Coverage: $COVERAGE"
                echo "COVERAGE=$COVERAGE" >> $GITHUB_ENV
            """.trimIndent()
    )

    run(
      name = "Update GitHub Summary",
      command = $$"""
        echo "## Code Coverage Report" >> $GITHUB_STEP_SUMMARY
        echo "**Total Coverage:** $COVERAGE%" >> $GITHUB_STEP_SUMMARY
      """.trimIndent()
    )

    run(
      name = "Generate Coverage Badge",
      command = $$"""
        mkdir -p badge
        COLOR=$(
          if [ "$COVERAGE" -ge 90 ]; then
            echo "brightgreen"
          elif [ "$COVERAGE" -ge 70 ]; then
            echo "yellow"
          else
            echo "red"
          fi
        )
        URL="https://img.shields.io/badge/Coverage-$COVERAGE%25-$COLOR"
        curl -sS "$URL" -o badge/coverage-badge.svg
      """.trimIndent()
    )

    uses(
      name = "Deploy Badge to GitHub Pages",
      action = ActionsGhPages(
        githubToken = expr { secrets.GITHUB_TOKEN },
        publishDir = "badge",
        publishBranch = "gh-pages",
        allowEmptyCommit = false,
        forceOrphan = true
      )
    )
  }
}
