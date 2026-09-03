#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:Repository("https://central.sonatype.com/repository/maven-snapshots/")
@file:CompilerOptions("-Xmulti-dollar-interpolation")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:4.0.0")

@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v5")
@file:DependsOn("actions:upload-artifact:v4")
@file:DependsOn("gradle:actions__setup-gradle:v6")
@file:DependsOn("peaceiris:actions-gh-pages:v4")


import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.actions.peaceiris.ActionsGhPages
import io.github.typesafegithub.workflows.domain.Concurrency
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.Cron
import io.github.typesafegithub.workflows.domain.triggers.Schedule
import io.github.typesafegithub.workflows.domain.triggers.WorkflowDispatch
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow


workflow(
  name = "Mutation Testing",
  // Weekly rather than per push. The run takes ~13 minutes, and it shares a concurrency group with
  // the Kover workflow, so triggering it on every merge would park the coverage badge behind it —
  // and GitHub cancels the older pending run when a third push arrives. Dispatch it by hand after
  // a change worth measuring.
  on = listOf(Schedule(listOf(Cron("0 5 * * 1"))), WorkflowDispatch()),
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
      action = UploadArtifact(name = "mutation-report", path = listOf("app/build/reports/pitest")),
      // A failed run is exactly when the report is worth reading.
      condition = "always()"
    )
    run(
      name = "Calculate Test Strength",
      command = $$"""
                STRENGTH=$(./gradlew -q printTestStrength)
                SCORE=$(./gradlew -q printMutationScore)
                echo "Raw Test Strength: $STRENGTH, Raw Mutation Score: $SCORE"
                STRENGTH=$(printf "%.0f" "$STRENGTH")  # Round to integer
                SCORE=$(printf "%.0f" "$SCORE")
                echo "Rounded Test Strength: $STRENGTH, Rounded Mutation Score: $SCORE"
                echo "TEST_STRENGTH=$STRENGTH" >> $GITHUB_ENV
                echo "MUTATION_SCORE=$SCORE" >> $GITHUB_ENV
            """.trimIndent()
    )

    run(
      name = "Update GitHub Summary",
      command = $$"""
        echo "## Mutation Testing Report" >> $GITHUB_STEP_SUMMARY
        echo "**Test Strength:** $TEST_STRENGTH% (of the mutants a test ran, how many it caught)" >> $GITHUB_STEP_SUMMARY
        echo "**Mutation Score:** $MUTATION_SCORE% (of every mutant, including code no test reaches)" >> $GITHUB_STEP_SUMMARY
      """.trimIndent()
    )

    run(
      name = "Generate Test Strength Badge",
      command = $$"""
        mkdir -p badge
        COLOR=$(
          if [ "$TEST_STRENGTH" -ge 80 ]; then
            echo "brightgreen"
          elif [ "$TEST_STRENGTH" -ge 60 ]; then
            echo "yellow"
          else
            echo "red"
          fi
        )
        URL="https://img.shields.io/badge/Test%20Strength-$TEST_STRENGTH%25-$COLOR"
        # -f so an outage at shields.io fails the job instead of publishing its error page as
        # the badge, which plain -sS would do while still exiting 0.
        curl -fsS --retry 3 --retry-all-errors "$URL" -o badge/test-strength-badge.svg
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
