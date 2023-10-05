package com.example.aufmassmanageriso_basaran.data.local

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


/**
 * Form class for defining forms.
 * Add a new form by extending this class.
 * Define the form fields as [FormField] properties of the class.
 */
abstract class Form {

    private val _fields = MutableStateFlow(mapOf<String, FormField>())

    /**
     * The form fields of the form.
     */
    val fields = _fields.asStateFlow()

    /**
     * Add a new form field to the form.
     * This is the most basic, but most flexible way to add a form field.
     *
     * @param name The name of the form field.
     * @param initialValue The initial value of the form field.
     * @param isRequired Whether the form field is required.
     * @param keyboardType The keyboard type of the form field. [Text(default), Number, Email, etc.]
     * @param imeAction The IME action of the form field. [Enter(default), OK, Next, etc.]
     * @param cleanInput A function that cleans the input of the form field on every text change.
     * @param onValidateToError A function that takes the current value of the form field and returns an error message if the value is invalid. Return `null` if the value is valid.
     */
    fun addFormField(
        name: String,
        initialValue: String = "",
        isRequired: Boolean = false,
        keyboardType: KeyboardType = KeyboardType.Text,
        imeAction: ImeAction = ImeAction.Default,
        cleanInput: (activeInput: String) -> String = { activeInput -> activeInput },
        onValidateToError: (value: String) -> String? = { null }
    ): FormField {
        return FormField(
            initialValue, isRequired, keyboardType, imeAction, cleanInput, onValidateToError
        ).also { field ->
            _fields.update { fields -> fields + (name to field) }
        }
    }

    /**
     * Add a new form field to the form that only accepts positive integers.
     *
     * This is a specialization of [addFormField].
     * It has those predefined values:
     * * keyboardType is [KeyboardType.Number]
     * * cleanInput is [FormField.cleanInputJustNumbers]
     */
    fun addNumberFormField(
        name: String,
        initialValue: String = "",
        isRequired: Boolean = false,
        imeAction: ImeAction = ImeAction.Default,
        onValidateToError: (value: String) -> String? = { null }
    ) = addFormField(
        name,
        initialValue,
        isRequired,
        KeyboardType.Number,
        imeAction,
        FormField::cleanInputJustNumbers,
        onValidateToError
    )

    /**
     * Validates all form fields and returns whether the form is valid.
     *
     * Any subclass of [Form] can override this method to add additional validation logic.
     * It is recommended to call `super.validate()` for consistency.
     */
    open fun validate(): Boolean {
        return _fields.value.values.all { it.validate() }
    }


    /**
     * Clears all form fields.
     *
     * Any subclass of [Form] can override this method to add its own clear logic.
     * It is recommended to call `super.validate()` for consistency.
     */
    open fun clearFields() {
        _fields.value.values.forEach { it.updateText("") }
    }
}
