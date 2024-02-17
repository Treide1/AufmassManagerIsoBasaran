package com.example.aufmassmanageriso_basaran.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextBeforeSelection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.aufmassmanageriso_basaran.data.local.DecimalSummingField
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DecimalSummingInput(
    field: DecimalSummingField,
    _forceOpen: Boolean = false
) {
    val text by field.text.collectAsState("")
    var isOpen by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = {},
        enabled = false,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable { isOpen = true },
        readOnly = true,
        label = {
            Text(
                buildAnnotatedString {
                    append(field.name)
                    if (field.isRequired) withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            )
        }
    )

    if (isOpen || _forceOpen) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnClickOutside = false)
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                // Display current text
                val focusRequester = remember { FocusRequester() }
                var textFieldValueState by remember { mutableStateOf(TextFieldValue(text)) }

                fun insertChar(char: String) {
                    val selection = textFieldValueState.selection
                    val current = textFieldValueState.text
                    val prefix = current.substring(0, selection.start)
                    val suffix = current.substring(selection.end)
                    val newText = prefix + char + suffix
                    val newSelection = TextRange(prefix.length + 1)
                    textFieldValueState = TextFieldValue(text = newText, selection = newSelection)
                }

                fun delChar() {
                    val selection = textFieldValueState.selection
                    val current = textFieldValueState.text
                    val prefix = current.substring(0, selection.start)
                    val suffix = current.substring(selection.end)
                    val newText = prefix.dropLast(1) + suffix
                    val newSelection = TextRange(prefix.length - 1)
                    textFieldValueState = TextFieldValue(text = newText, selection = newSelection)
                }

                TextField(
                    value = textFieldValueState.text,
                    onValueChange = { },
                    label = { Text(field.name) },
                    modifier = Modifier.focusRequester(focusRequester)
                )
                LaunchedEffect(key1 = null) {
                    focusRequester.requestFocus()
                }


                // Grid of num pad and, plus, decimal point and okay button
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(4),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Col #1
                    item {
                        DecimalInputButton("+", ::insertChar)
                    }
                    item {
                        DecimalInputButton("1", ::insertChar)
                    }
                    item {
                        DecimalInputButton("4", ::insertChar)
                    }
                    item {
                        DecimalInputButton("7", ::insertChar)
                    }

                    // Col #2
                    item {
                        DecimalInputButton("0", ::insertChar)
                    }
                    item {
                        DecimalInputButton("2", ::insertChar)
                    }
                    item {
                        DecimalInputButton("5", ::insertChar)
                    }
                    item {
                        DecimalInputButton("8", ::insertChar)
                    }

                    // Col #3
                    item {
                        DecimalInputButton(",", ::insertChar)
                    }
                    item {
                        DecimalInputButton("3", ::insertChar)
                    }
                    item {
                        DecimalInputButton("6", ::insertChar)
                    }
                    item {
                        DecimalInputButton("9", ::insertChar)
                    }

                    // Col #4
                    item(span = { GridItemSpan(2) }) {
                        InputButton(text = "⌫", onClick = ::delChar)
                    }
                    item(span = { GridItemSpan(2) }) {
                        InputButton(text = "Okay") {
                            isOpen = false
                        }
                    }
                }

            }

        }
    }
}

@Composable
fun DecimalInputButton(char: String, onClick: (String) -> Unit) {
    InputButton(text = char) {
        onClick(char)
    }
}

@Composable
fun InputButton(text: String, onClick: () -> Unit) {
    TextButton(
        onClick =  onClick,
        modifier = Modifier.padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun DecimalSummingFieldPreview() {
    AufmassManagerIsoBasaranTheme {
        Surface {
            DecimalSummingInput(
                field = DecimalSummingField(
                    name = "Decimal Summing Field"
                ),
                _forceOpen = true
            )
        }
    }
}