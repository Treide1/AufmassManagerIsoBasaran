package com.example.aufmassmanageriso_basaran.data.local

import kotlinx.coroutines.flow.update
import kotlin.reflect.KProperty

class DecimalSummingField(
    name: String,
    initialValue: String = "",
    isRequired: Boolean = false,
    isRetained: Boolean = false,
    onValidateToError: (String) -> String? = { null }
): FormField(
    name,
    initialValue,
    isRequired,
    isRetained,
    onValidateToError
)  {

    operator fun getValue(any: Any?, property: KProperty<*>): String {
        return _text.value
    }

    operator fun setValue(any: Any?, property: KProperty<*>, value: String) {
        _text.value = value
    }

    fun setText(value: String) {
        _text.update { value }
    }
}