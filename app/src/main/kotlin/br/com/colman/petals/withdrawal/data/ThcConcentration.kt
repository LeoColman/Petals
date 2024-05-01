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
 * Data points approximation from Figure 1. Mean creatinine-normalized tetrahydrocannabinol level
 */
val ThcConcentrationDataPoints = mapOf(
  days(0) to 250.0,
  days(1) to 175.0,
  days(2) to 125.0,
  days(3) to 100.0,
  days(4) to 75.0,
  days(7) to 50.0,
  days(10) to 45.0,
  days(14) to 25.0,
  days(20) to 0.0 // Approximate based on Table 2
)
