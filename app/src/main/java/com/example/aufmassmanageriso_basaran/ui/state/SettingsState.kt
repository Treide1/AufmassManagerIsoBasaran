package com.example.aufmassmanageriso_basaran.ui.state

data class SettingsState(
    val bauvorhaben: String = "",
    val aufmassNummer: Int? = 1,
    val aufmassDatum: String = "",
    val auftragsNummer: Int? = null,
    val allgemeineNotiz: String = ""
)
