package com.example.aufmassmanageriso_basaran.data.local

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.aufmassmanageriso_basaran.logging.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


/**
 * Form class for defining forms.
 * Add a new form by extending this class.
 * Define the form fields as [TextFormField] properties of the class.
 */
abstract class Form {

    companion object {
        /**
         * Tag for logging.
         */
        private val logger = Logger("Form")
    }

    private val _fields = MutableStateFlow(listOf<FormField>())

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
    fun addTextFormField(
        name: String,
        initialValue: String = "",
        isRequired: Boolean = false,
        isRetained: Boolean = false,
        keyboardType: KeyboardType = KeyboardType.Text,
        imeAction: ImeAction = ImeAction.Default,
        cleanInput: (activeInput: String) -> String = { activeInput -> activeInput },
        onValidateToError: (value: String) -> String? = { null }
    ) = TextFormField(
        name,
        initialValue,
        isRequired,
        isRetained,
        keyboardType,
        imeAction,
        cleanInput,
        onValidateToError
    ).also { _fields.update { fields -> fields + it } }


    /**
     * Add a new form field to the form that only accepts positive integers.
     *
     * This is a specialization of [addTextFormField].
     * It has those predefined values:
     * * keyboardType is [KeyboardType.Number]
     * * cleanInput is [TextFormField.cleanInputJustNumbers]
     */
    fun addIntFormField(
        name: String,
        initialValue: String = "",
        isRequired: Boolean = false,
        isRetained: Boolean = false,
        imeAction: ImeAction = ImeAction.Next,
        onValidateToError: (value: String) -> String? = { null }
    ) = addTextFormField(
        name,
        initialValue,
        isRequired,
        isRetained,
        KeyboardType.Number,
        imeAction,
        TextFormField::cleanInputJustNumbers,
        onValidateToError
    )

    fun addDecimalSummingFormField(
        name: String,
        initialValue: String = "",
        isRequired: Boolean = false,
        isRetained: Boolean = false,
        onValidateToError: (value: String) -> String? = { null }
    ): DecimalSummingField = DecimalSummingField(
        name,
        initialValue,
        isRequired,
        isRetained,
        onValidateToError
    ).also { _fields.update { fields -> fields + it } }

    fun addDerivedFormField(
        name: String,
        derivedText: Flow<String>
    ): DerivedFormField = DerivedFormField(
        name,
        derivedText
    ).also { _fields.update { fields -> fields + it } }

    /**
     * Validates all form fields and returns whether the form is valid.
     *
     * Any subclass of [Form] can override this method to add additional validation logic.
     * It is recommended to call `super.validate()` for consistency.
     */
    open fun validate(): Boolean {
        logger.d("validate: Validating. fields=${_fields.value}")
        return _fields.value.all { it.validate() }
    }


    /**
     * Clears all form fields.
     *
     * Any subclass of [Form] can override this method to add its own clear logic.
     * It is recommended to call `super.validate()` for consistency.
     */
    open fun clearFields() {
        _fields.value.forEach { it.clearIfNotRetained() }
    }
}
