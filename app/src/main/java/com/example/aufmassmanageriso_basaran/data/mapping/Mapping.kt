@file:Suppress("unused")

package com.example.aufmassmanageriso_basaran.data.mapping

import com.example.aufmassmanageriso_basaran.data.local.BauvorhabenForm
import com.example.aufmassmanageriso_basaran.data.local.EintragForm
import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.BauvorhabenDto
import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.EintragDto
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

fun EintragForm.toDto() = EintragDto(
    bereich = bereich ?: "[KeinBereich]",
    durchmesser = durchmesser.toInt(),
    isolierung = isolierung,
    gewerk = gewerk,
    meterListe = meterListe.map { it.toDouble() },
    meterSumme = meterSumme.toDouble(),
    bogen = bogen.toInt(),
    stutzen = stutzen.toInt(),
    ausschnitt = ausschnitt.toInt(),
    passstueck = passstueck.toInt(),
    endstelle = endstelle.toInt(),
    halter = halter.toInt(),
    flansch = flansch.toInt(),
    ventil = ventil.toInt(),
    schmutzfilter = schmutzfilter.toInt(),
    dreiWegeVentil = dreiWegeVentil.toInt(),
    notiz = notiz.ifBlank { null }
)