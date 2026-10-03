package br.com.colman.petals.utils

import java.math.BigDecimal

/**
 * Reads a decimal whose plain form stays short, or null. BigDecimal reads "1E-999999999" happily, but writing it out
 * with toPlainString, as the database and the CSV do, takes a billion digits and runs out of memory. Exponents that
 * far out are refused, while "1e-3", ".5" or "+2" still read.
 */
fun String.toBoundedDecimalOrNull(): BigDecimal? = toBigDecimalOrNull()?.takeIf { it.scale() in -MaxScale..MaxScale }

private const val MaxScale = 32
