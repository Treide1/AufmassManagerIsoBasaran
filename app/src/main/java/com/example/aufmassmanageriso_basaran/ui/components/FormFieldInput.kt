package com.example.aufmassmanageriso_basaran.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.data.local.FormField

/**
 * Outlined text input with validation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFieldInput(
    name: String,
    formField: FormField
) {
    val text by formField.text.collectAsState()
    OutlinedTextField(
        value = text,
        onValueChange = { formField.updateText(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        label = {
            Text(
                buildAnnotatedString {
                    append(name)
                    if (formField.isRequired) withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = formField.keyboardType,
            imeAction = formField.imeAction
        )
    )
}