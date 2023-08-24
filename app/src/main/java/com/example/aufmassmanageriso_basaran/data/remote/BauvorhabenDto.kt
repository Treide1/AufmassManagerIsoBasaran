package com.example.aufmassmanageriso_basaran.data.remote

/**
 * Data transfer object for table "Bauvorhaben".
 */
data class BauvorhabenDto(
    val bauvorhaben: String,
    val aufmassNummer: Long,
    val auftragsNummer: Long?,
    val notiz: String?
)
