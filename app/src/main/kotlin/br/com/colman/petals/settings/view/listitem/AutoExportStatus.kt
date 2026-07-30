package br.com.colman.petals.settings.view.listitem

/**
 * Bundles the three DataStore-derived status fields into one parameter, so
 * [AutoExportListItem] stays under detekt's LongParameterList threshold.
 */
data class AutoExportStatus(
  val folderName: String?,
  val lastSuccessAtEpochMillis: Long?,
  val lastError: String?,
)
