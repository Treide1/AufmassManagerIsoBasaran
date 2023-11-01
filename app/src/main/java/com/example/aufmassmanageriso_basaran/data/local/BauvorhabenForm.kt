package com.example.aufmassmanageriso_basaran.data.local

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

/**
 * Form class for a new construction project (german: "Bauvorhaben").
 */
class BauvorhabenForm: Form() {

    var name by addTextFormField(
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

    var aufmassNummer by addTextFormField(
        "Aufmaß-Nummer",
        isRequired = true,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next,
        cleanInput = TextFormField::cleanInputJustNumbers,
        onValidateToError = { value ->
            when {
                value.isBlank() -> "Bitte geben Sie eine Aufmass-Nummer ein"
                value.toIntOrNull() == null -> "Bitte geben Sie eine gültige Nummer ein"
                else -> null
            }
        }
    )

    var auftragsNummer by addTextFormField(
        "Auftrags-Nummer",
        isRequired = false,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next,
        cleanInput = TextFormField::cleanInputJustNumbers
    )

    var notiz by addTextFormField(
        "Notiz",
        isRequired = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    )
}