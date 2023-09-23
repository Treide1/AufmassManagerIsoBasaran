package com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben

/**
 * Data transfer object for collection "bauvorhaben".
 */
data class BauvorhabenDto(
    val name: String,
    val aufmassNummer: Long,
    val auftragsNummer: Long?,
    val notiz: String?
)
