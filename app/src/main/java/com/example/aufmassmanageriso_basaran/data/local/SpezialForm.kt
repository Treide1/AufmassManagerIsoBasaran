package com.example.aufmassmanageriso_basaran.data.local

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

/**
 * Form class for a new standard entry (german: "Eintrag").
 *
 * `SpezialEintrag` (remote model)
 *   * `daten`: string
 *   * `notiz`: string
 *   * `zeitstempel`: timestamp (automatisch generiert)
 */
class SpezialForm: Form() {

    // Required fields
    var bereich by addTextFormField(
        name = "Bereich",
        isRequired = true,
        isRetained = true,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next,
        onValidateToError = TextFormField::validateNotBlank
    )

    var daten by addTextFormField(
        name = "Daten zum Objekt",
        isRequired = true,
        isRetained = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next,
        onValidateToError = TextFormField::validateNotBlank
    )

    // Optional fields
    var notiz by addTextFormField(
        name = "Notiz",
        isRequired = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    )

}