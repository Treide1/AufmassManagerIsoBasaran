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
    bereich = bereich,
    durchmesser = durchmesser.toInt(),
    isolierung = isolierung,
    gewerk = gewerk,
    // Blank entries as 0 or 0.0
    meterListe = convertInputMeterListe(meterListe),
    meterSumme = convertInputMeterListe(meterListe).sum(),
    bogen = bogen.toIntBlankAsZero(),
    stutzen = stutzen.toIntBlankAsZero(),
    ausschnitt = ausschnitt.toIntBlankAsZero(),
    passstueck = passstueck.toIntBlankAsZero(),
    endstelle = endstelle.toIntBlankAsZero(),
    halter = halter.toIntBlankAsZero(),
    flansch = flansch.toIntBlankAsZero(),
    ventil = ventil.toIntBlankAsZero(),
    schmutzfilter = schmutzfilter.toIntBlankAsZero(),
    dreiWegeVentil = dreiWegeVentil.toIntBlankAsZero(),
    notiz = notiz.ifBlank { null }
)