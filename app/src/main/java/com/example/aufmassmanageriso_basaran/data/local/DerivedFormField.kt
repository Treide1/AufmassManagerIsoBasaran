package com.example.aufmassmanageriso_basaran.data.local

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KProperty

class DerivedFormField(
    name: String,
    val derivedText: Flow<String>
): FormField(
    name
) {
    operator fun getValue(any: Any?, property: KProperty<*>): String {
        return "[]"
    }
}