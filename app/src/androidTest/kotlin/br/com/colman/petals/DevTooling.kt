package br.com.colman.petals

/**
 * Marks a spec that drives developer tooling rather than verifying behaviour, so it needs something
 * CI does not have: a listening machine, a checked-in artifact, a human reading the output.
 *
 * Excluded from automated runs with:
 *
 *     -Pandroid.testInstrumentationRunnerArguments.notAnnotation=br.com.colman.petals.DevTooling
 *
 * The marker lives on the class rather than in the workflow so the reason travels with the test.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class DevTooling
