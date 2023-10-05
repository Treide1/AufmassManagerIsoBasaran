package com.example.aufmassmanageriso_basaran.data.local

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

/**
 * Form class for a new standard entry (german: "Eintrag").
 *
 * `Eintrag` (remote model)
 *   * `bereich`: string
 *   * `durchmesser`: number
 *   * `isolierung`: string
 *   * `gewerk`: string
 *   * `meterListe`: array<number>
 *   * `meterSumme`: number
 *   * `bogen`: number
 *   * `stutzen`: number
 *   * `ausschnitt`: number
 *   * `passstueck`: number
 *   * `endstelle`: number
 *   * `halter`: number
 *   * `flansch`: number
 *   * `ventil`: number
 *   * `schmutzfilter`: number
 *   * `dreiWegeVentil`: number
 *   * `notiz`: string
 *   * `zeitstempel`: timestamp (automatisch generiert)
 */
class EintragForm(
    val bereich: String,
): Form() {

    // Required fields
    var durchmesser by addFormField(
        "Durchmesser (ø)",
        isRequired = true,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next,
        cleanInput = FormField::cleanInputJustNumbers,
        onValidateToError = FormField::validateNotBlank
    )

    var isolierung by addFormField(
        "Isolierung",
        isRequired = true,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next,
        onValidateToError = FormField::validateNotBlank
    )

    var gewerk by addFormField(
        "Gewerk",
        isRequired = true,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next,
        onValidateToError = FormField::validateNotBlank
    )

    // Optional fields
    // TODO: Add meterListe
    // TODO: Add meterSumme derived by sum

    var bogen by addNumberFormField("Bogen")
    var stutzen by addNumberFormField("Stutzen")
    var ausschnitt by addNumberFormField("Ausschnitt")
    var passstueck by addNumberFormField("Passstück")
    var endstelle by addNumberFormField("Endstelle")
    var halter by addNumberFormField("Halter")
    var flansch by addNumberFormField("Flansch")
    var ventil by addNumberFormField("Ventil")
    var schmutzfilter by addNumberFormField("Schmutzfilter")
    var dreiWegeVentil by addNumberFormField("Drei-Wege-Ventil")

    var notiz by addFormField(
        "Notiz",
        isRequired = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    )

}