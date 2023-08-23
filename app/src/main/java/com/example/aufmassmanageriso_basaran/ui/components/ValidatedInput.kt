package com.example.aufmassmanageriso_basaran.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

/**
 * Outlined text input with validation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidatedInput(
    labelText: String,
    isRequired: Boolean,
    keyboardType: KeyboardType,
    getDefaultValue: () -> String,
    cleanInput: (String) -> String = { activeInput -> activeInput },
    isValidOnConfirm: (String) -> Boolean = { confirmedInput -> confirmedInput.isNotBlank() },
    onValid: (String) -> Unit = { validInput -> println("validInput: $validInput") },
) {
    var textValue by remember { mutableStateOf(getDefaultValue()) }
    OutlinedTextField(
        value = textValue,
        onValueChange = {
            textValue = cleanInput(it)
        },
        modifier = Modifier.padding(bottom = 16.dp),
        label = {
            Text(
                buildAnnotatedString {
                    append(labelText)
                    if (isRequired) withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                if (isValidOnConfirm(textValue)) {
                    onValid(textValue)
                    this.defaultKeyboardAction(ImeAction.Next)
                } else {
                    textValue = getDefaultValue()
                }
            }
        )
    )
}