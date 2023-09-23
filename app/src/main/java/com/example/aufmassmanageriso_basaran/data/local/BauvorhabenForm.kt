package com.example.aufmassmanageriso_basaran.data.local

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

class BauvorhabenForm: Form() {
    var name by addFormField(
        "Name des Bauvorhaben",
        isRequired = true,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next,
        onValidateToError = { value ->
            when {
                value.isBlank() -> "Bitte geben Sie ein Bauvorhaben ein"
                else -> null
            }
        },
    )

    var aufmassNummer by addFormField(
        "Aufmaß-Nummer",
        isRequired = true,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next,
        cleanInput = FormField::cleanInputJustNumbers,
        onValidateToError = { value ->
            when {
                value.isBlank() -> "Bitte geben Sie eine Aufmass-Nummer ein"
                value.toIntOrNull() == null -> "Bitte geben Sie eine gültige Nummer ein"
                else -> null
            }
        }
    )

    var auftragsNummer by addFormField(
        "Auftrags-Nummer",
        isRequired = false,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next,
        cleanInput = FormField::cleanInputJustNumbers
    )

    var notiz by addFormField(
        "Notiz",
        isRequired = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    )
}