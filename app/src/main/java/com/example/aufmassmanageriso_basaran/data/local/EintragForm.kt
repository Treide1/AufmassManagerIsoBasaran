package com.example.aufmassmanageriso_basaran.data.local

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.aufmassmanageriso_basaran.data.mapping.convertInputMeterListe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest

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
 *   * `schmutzfaenger`: number
 *   * `dreiWegeVentil`: number
 *   * `notiz`: string
 *   * `zeitstempel`: timestamp (automatisch generiert)
 */
class EintragForm: Form() {

    // Required fields
    var bereich by addTextFormField(
        name = "Bereich",
        isRequired = true,
        isRetained = true,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next,
        onValidateToError = TextFormField::validateNotBlank
    )

    var durchmesser by addTextFormField(
        name = "Durchmesser (ø)",
        isRequired = true,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next,
        cleanInput = TextFormField::cleanInputJustNumbers,
        onValidateToError = TextFormField::validateNotBlank
    )

    var isolierung by addTextFormField(
        name = "Isolierung",
        isRequired = true,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next,
        onValidateToError = TextFormField::validateNotBlank
    )

    var gewerk by addTextFormField(
        name = "Gewerk",
        isRequired = true,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next,
        onValidateToError = TextFormField::validateNotBlank
    )

    // Optional fields
    val meterListeFormField = addDecimalSummingFormField("Meter-Liste")
    var meterListe by meterListeFormField

    @Suppress("unused") // Justify: Still visually added as form field
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val meterSumme by addDerivedFormField(
        name = "Meter-Summe",
        derivedText = meterListeFormField._text
            .debounce(450L)
            .mapLatest { activeInput ->
                try {
                    convertInputMeterListe(activeInput).sum().toString()
                } catch (e: Exception) {
                    "[Fehler in Summe]"
                }
            }
    )

    var bogen by addIntFormField("Bogen")
    var stutzen by addIntFormField("Stutzen")
    var ausschnitt by addIntFormField("Ausschnitt")
    var passstueck by addIntFormField("Passstück")
    var endstelle by addIntFormField("Endstelle")
    var halter by addIntFormField("Halter")
    var flansch by addIntFormField("Flansch")
    var ventil by addIntFormField("Ventil")
    var schmutzfaenger by addIntFormField("Schmutzfänger")
    var dreiWegeVentil by addIntFormField("Drei-Wege-Ventil")

    var notiz by addTextFormField(
        name = "Notiz",
        isRequired = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    )

}