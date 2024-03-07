package com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben

import com.google.firebase.Timestamp

/**
 * Data transfer object for collection "bauvorhaben".
 */
@Suppress("PropertyName")
data class BauvorhabenDto(
    val name: String,
    val aufmassNummer: Long,
    val auftragsNummer: Long?,
    val notiz: String?,
    val _docID: String? = null,
    val _zeitstempel: Timestamp? = null,
    val _letzterExportZeitstempel: Timestamp? = null
)
