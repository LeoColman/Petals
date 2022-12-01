package br.com.colman.petals.utils

import androidx.annotation.PluralsRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun pluralResource(
  @PluralsRes resId: Int,
  quantity: Int,
  vararg formatArgs: Any? = emptyArray()
) = LocalContext.current.resources.getQuantityString(resId, quantity, *formatArgs)
