package com.example.aufmassmanageriso_basaran.data.local

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.flow.update
import kotlin.reflect.KProperty

/**
 * Form field class for defining text-based input component.
 *
 * @param initialValue The initial value of the form field.
 * @param onValidateToError A function that takes the current value of the form field and returns an error message if the value is invalid.
 */
class TextFormField internal constructor(
    name: String,
    initialValue: String = "",
    isRequired: Boolean = false,
    isRetained: Boolean = false,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val imeAction: ImeAction = ImeAction.Default,
    val cleanInput: (activeInput: String) -> String = { activeInput -> activeInput },
    override val onValidateToError: (String) -> String? = { null }
): FormField(
    name,
    initialValue,
    isRequired,
    isRetained,
    onValidateToError
) {

    operator fun getValue(any: Any?, property: KProperty<*>): String {
        return _text.value
    }

    operator fun setValue(any: Any?, property: KProperty<*>, value: String) {
        _text.value = cleanInput(value)
    }

    fun updateText(value: String) {
        _text.update { cleanInput(value) }
    }

    companion object {
        fun cleanInputJustNumbers(activeInput: String): String {
            return activeInput.replace(Regex("[^0-9]"), "")
        }

        fun cleanInputJustDecimalsAndPlus(activeInput: String): String {
            return activeInput
                .replace(Regex("[.,]"), ".")
                .replace(Regex("[^0-9.+]"), "")
        }

        val DECIMAL_SEPARATOR_REGEX = Regex("[.,]")
        val DECIMAL_CHAR = '.'
        val ADDITION_CHAR = '+'

        /**
         * Clean the input of a form field that sums up non-negative decimal numbers.
         * The input is cleaned by removing all non-digit characters except for the decimal separator and '+'.
         * Any decimal separator is replaced by a dot.
         */
        fun cleanInputSummingDecimals(activeInput: String): String {
            val sb = StringBuilder()
            var hasCurrentDecimalASeparator = false
            var lastChar = '+' // Starts off true as if we start with "+"
            for (c in activeInput) {
                // A digit is always fine
                if (c.isDigit()) {
                    sb.append(c)
                    lastChar = c
                } else
                    if (c.toString().matches(DECIMAL_SEPARATOR_REGEX)) {
                        // A decimal separator is fine, if the last symbol was not a decimal separator
                        if (!hasCurrentDecimalASeparator) {
                            sb.append(DECIMAL_CHAR)
                            hasCurrentDecimalASeparator = true
                            lastChar = DECIMAL_CHAR
                        }
                } else
                    if (c == ADDITION_CHAR) {
                        // The plus operator is fine if the last symbol was not a plus operator.
                        if (lastChar == ADDITION_CHAR) continue
                        // In case of a missing decimal separator, we need to add it.
                        if (!hasCurrentDecimalASeparator) {
                            sb.append(DECIMAL_CHAR)
                            hasCurrentDecimalASeparator = true
                            lastChar = DECIMAL_CHAR
                        }
                        // In case of a missing digit, we need to add a zero.
                        if (lastChar == DECIMAL_CHAR) {
                            sb.append('0')
                            sb.append(c)
                            // Thus, the last symbol was an operator and we start a new number
                            lastChar = ADDITION_CHAR
                            hasCurrentDecimalASeparator = false
                        }
                }
            }
            return sb.toString()
        }

        fun validateNotBlank(value: String): String? {
            return when {
                value.isBlank() -> "Bitte fülle dieses Feld aus."
                else -> null
            }
        }
    }
}