package br.com.colman.petals.withdrawal.data

import java.time.Duration.ofDays as days

/**
 * (Budney, J Abnorm Psychol, 2003)
 *
 * DOI 10.1037/0021-843x.112.3.393
 *
 * The article gives the data in days, and we'll transform them here.
 * Baseline/0.0 indicates the starting point when an individual starts the abstinence period
 *
 * Values approximated from Figure 2
 */
val SleepDifficultyDataPoints = mapOf(
  days(-1) to 0.5,
  days(0) to 0.5,
  days(2) to 1.0,
  days(5) to 0.87,
  days(8) to 0.90,
  days(11) to 0.53,
  days(14) to 0.56,
  days(17) to 0.97,
  days(20) to 0.85,
  days(23) to 0.53,
  days(25) to 0.53,
)
