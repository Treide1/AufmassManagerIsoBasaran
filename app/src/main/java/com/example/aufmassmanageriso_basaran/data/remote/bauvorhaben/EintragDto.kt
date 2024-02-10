package com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben

/**
 * Data transfer object for collection "eintraege".
 */
data class EintragDto(
    val bereich: String,
    val durchmesser: Int,
    val isolierung: String,
    val gewerk: String,
    val meterListe: List<Double>,
    val meterSumme: Double,
    val bogen: Int,
    val stutzen: Int,
    val ausschnitt: Int,
    val passstueck: Int,
    val endstelle: Int,
    val halter: Int,
    val flansch: Int,
    val ventil: Int,
    val schmutzfaenger: Int,
    val dreiWegeVentil: Int,
    val notiz: String?
)
