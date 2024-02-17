package com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben

/**
 * Data transfer object for collection "bauvorhaben".
 */
@Suppress("PropertyName")
data class BauvorhabenDto(
    val name: String,
    val aufmassNummer: Long,
    val auftragsNummer: Long?,
    val notiz: String?,
    val _docID: String? = null
)
