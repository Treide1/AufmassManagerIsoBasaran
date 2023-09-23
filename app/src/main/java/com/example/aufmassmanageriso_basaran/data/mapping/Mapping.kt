@file:Suppress("unused")

package com.example.aufmassmanageriso_basaran.data.mapping

import com.example.aufmassmanageriso_basaran.data.local.BauvorhabenForm
import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.BauvorhabenDto
import com.google.firebase.firestore.DocumentSnapshot

fun BauvorhabenForm.toDto() = BauvorhabenDto(
    name = name,
    aufmassNummer = aufmassNummer.toLong(),
    auftragsNummer = auftragsNummer.toLongOrNull(),
    notiz = notiz.ifBlank { null }
)

fun BauvorhabenDto.toForm(): BauvorhabenForm {
    val dto = this
    return BauvorhabenForm().apply {
        name = dto.name
        aufmassNummer = dto.aufmassNummer.toString()
        auftragsNummer = dto.auftragsNummer?.toString() ?: ""
        notiz = dto.notiz ?: ""
    }
}

fun DocumentSnapshot.toDto(): BauvorhabenDto {
    val doc = this
    return BauvorhabenDto(
        name = doc["name"] as String,
        aufmassNummer = doc["aufmassNummer"] as Long,
        auftragsNummer = doc["auftragsNummer"] as Long?,
        notiz = doc["notiz"] as String?
    )
}