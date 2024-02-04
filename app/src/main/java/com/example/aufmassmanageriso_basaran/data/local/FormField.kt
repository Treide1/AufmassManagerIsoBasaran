package com.example.aufmassmanageriso_basaran.data.local

import com.example.aufmassmanageriso_basaran.logging.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


abstract class FormField(
    val name: String,
    initialValue: String = "",
    val isRequired: Boolean = false,
    val isRetained: Boolean = false,
    open val onValidateToError: (textValue: String) -> String? = { null }
) {

    companion object {
        /**
         * Tag for logging.
         */
        private val logger = Logger("FormField")
    }

    internal val _text = MutableStateFlow(initialValue)
    val text = _text.asStateFlow()

    var error: String? = null

    fun validate(): Boolean {
        error = onValidateToError(text.value)
        logger.d("validate: Validating. name=$name, text=${text.value} => error = $error")
        return error == null
    }

    fun clearIfNotRetained() {
        if (!isRetained) {
            _text.update { "" }
        }
    }
}