package com.example.aufmassmanageriso_basaran.data.local

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.reflect.KProperty

/**
 * Form field class for defining form fields.
 *
 * @param initialValue The initial value of the form field.
 * @param onValidateToError A function that takes the current value of the form field and returns an error message if the value is invalid.
 */
class FormField internal constructor(
    initialValue: String = "",
    val isRequired: Boolean = false,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val imeAction: ImeAction = ImeAction.Default,
    val cleanInput: (activeInput: String) -> String = { activeInput -> activeInput },
    val onValidateToError: (value: String) -> String? = { null }
) {
    private val _text = MutableStateFlow(initialValue)
    var text = _text.asStateFlow()

    var error: String? = null

    fun validate(): Boolean {
        error = onValidateToError(text.value)
        return error == null
    }

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

        fun validateNotBlank(value: String): String? {
            return when {
                value.isBlank() -> "Bitte fülle dieses Feld aus."
                else -> null
            }
        }
    }
}