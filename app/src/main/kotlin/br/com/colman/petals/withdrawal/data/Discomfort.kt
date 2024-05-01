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
val DiscomfortDataPoints = mapOf(
  days(-1) to 3.5,
  days(0) to 3.5,
  days(2) to 7.5,
  days(5) to 7.3,
  days(8) to 6.6,
  days(11) to 5.3,
  days(14) to 5.0,
  days(17) to 5.0,
  days(20) to 4.0,
  days(23) to 3.5,
  days(25) to 3.0,
)
