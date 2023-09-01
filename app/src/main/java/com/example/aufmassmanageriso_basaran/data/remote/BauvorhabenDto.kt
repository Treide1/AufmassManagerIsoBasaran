package com.example.aufmassmanageriso_basaran.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data transfer object for table "Bauvorhaben".
 * It's parcelable (can be passed between processes).
 */
@Parcelize
data class BauvorhabenDto(
    val bauvorhaben: String,
    val aufmassNummer: Long,
    val auftragsNummer: Long?,
    val notiz: String?
): Parcelable
