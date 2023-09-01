@file:Suppress("unused")

package com.example.aufmassmanageriso_basaran.data.mapping

import com.example.aufmassmanageriso_basaran.data.local.BauvorhabenForm
import com.example.aufmassmanageriso_basaran.data.remote.BauvorhabenDto
import com.google.firebase.firestore.DocumentSnapshot

fun BauvorhabenForm.toDto() = BauvorhabenDto(
    bauvorhaben = bauvorhaben,
    aufmassNummer = aufmassNummer.toLong(),
    auftragsNummer = auftragsNummer.toLongOrNull(),
    notiz = notiz.ifBlank { null }
)

fun BauvorhabenDto.toForm(): BauvorhabenForm {
    val dto = this
    return BauvorhabenForm().apply {
        bauvorhaben = dto.bauvorhaben
        aufmassNummer = dto.aufmassNummer.toString()
        auftragsNummer = dto.auftragsNummer?.toString() ?: ""
        notiz = dto.notiz ?: ""
    }
}

fun DocumentSnapshot.toDto(): BauvorhabenDto {
    val doc = this
    return BauvorhabenDto(
        bauvorhaben = doc["bauvorhaben"] as String,
        aufmassNummer = doc["aufmassNummer"] as Long,
        auftragsNummer = doc["auftragsNummer"] as Long?,
        notiz = doc["notiz"] as String?
    )
}