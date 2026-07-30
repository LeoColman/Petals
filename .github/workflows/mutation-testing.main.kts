#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:Repository("https://central.sonatype.com/repository/maven-snapshots/")
@file:CompilerOptions("-Xmulti-dollar-interpolation")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:4.0.0")

@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v5")
@file:DependsOn("actions:upload-artifact:v4")
@file:DependsOn("gradle:actions__setup-gradle:v4")
@file:DependsOn("peaceiris:actions-gh-pages:v4")


import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.actions.peaceiris.ActionsGhPages
import io.github.typesafegithub.workflows.domain.Concurrency
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.domain.triggers.WorkflowDispatch
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow


workflow(
  name = "Mutation Testing",
  on = listOf(Push(branches = listOf("main")), WorkflowDispatch()),
  // Shared with the Kover workflow: both push a badge to the same gh-pages branch.
  concurrency = Concurrency(group = "gh-pages-badges", cancelInProgress = false),
  sourceFile = __FILE__
) {
  job(id = "mutation-testing", runsOn = RunnerType.UbuntuLatest) {
    uses(name = "Setup JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(name = "Checkout", action = Checkout())
    uses(name = "Setup Gradle", action = ActionsSetupGradle())

    run(name = "Run PIT Mutation Testing", command = "./gradlew pitest")
    uses(
      name = "Upload Mutation Report",
      action = UploadArtifact(name = "mutation-report", path = listOf("app/build/reports/pitest"))
    )
    run(
      name = "Calculate Mutation Score",
      command = $$"""
                SCORE=$(./gradlew -q printMutationScore)
                echo "Raw Mutation Score: $SCORE"
                SCORE=$(printf "%.0f" "$SCORE")  # Round to integer
                echo "Rounded Mutation Score: $SCORE"
                echo "MUTATION_SCORE=$SCORE" >> $GITHUB_ENV
            """.trimIndent()
    )

    run(
      name = "Update GitHub Summary",
      command = $$"""
        echo "## Mutation Testing Report" >> $GITHUB_STEP_SUMMARY
        echo "**Mutation Score:** $MUTATION_SCORE%" >> $GITHUB_STEP_SUMMARY
      """.trimIndent()
    )

    run(
      name = "Generate Mutation Badge",
      command = $$"""
        mkdir -p badge
        COLOR=$(
          if [ "$MUTATION_SCORE" -ge 80 ]; then
            echo "brightgreen"
          elif [ "$MUTATION_SCORE" -ge 60 ]; then
            echo "yellow"
          else
            echo "red"
          fi
        )
        URL="https://img.shields.io/badge/Mutation%20Score-$MUTATION_SCORE%25-$COLOR"
        curl -sS "$URL" -o badge/mutation-badge.svg
      """.trimIndent()
    )

    uses(
      name = "Deploy Badge to GitHub Pages",
      action = ActionsGhPages(
        githubToken = expr { secrets.GITHUB_TOKEN },
        publishDir = "badge",
        publishBranch = "gh-pages",
        allowEmptyCommit = false,
        keepFiles = true
      )
    )
  }
}
