package com.example.aufmassmanageriso_basaran.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextBeforeSelection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.aufmassmanageriso_basaran.data.local.DecimalSummingField
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecimalSummingInput(
    field: DecimalSummingField
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
        // Counter enabled=false by resetting the colors
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledBorderColor = MaterialTheme.colorScheme.onBackground,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
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

    if (isOpen) {
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
                DecimalInputDialogContent(
                    initialText = text,
                    labelName = field.name
                ) { updated ->
                    field.setText(updated)
                    isOpen = false
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecimalInputDialogContent(
    initialText: String,
    labelName: String,
    onValidTextUpdated: (String) -> Unit
) {
// Display current text
    val focusRequester = remember { FocusRequester() }
    var textFieldValueState by remember { mutableStateOf(
        TextFieldValue(initialText, TextRange(initialText.length))
    ) }
    var isTextValid by remember { mutableStateOf(true) }

    fun insertChar(char: String) {
        val selection = textFieldValueState.selection
        val current = textFieldValueState.text
        val prefix = current.substring(0, selection.start)
        val suffix = current.substring(selection.end)
        val newText = prefix + char + suffix
        val newSelection = TextRange(prefix.length + 1)
        textFieldValueState = TextFieldValue(text = newText, selection = newSelection)
        if (isTextValid.not()) {
            isTextValid = textFieldValueState.isValidDecimalSum()
        }
    }

    fun delChar() {
        val selection = textFieldValueState.selection
        val current = textFieldValueState.text

        val prefix = current.substring(0, selection.start)
        val suffix = current.substring(selection.end)
        val newText = prefix.dropLast(1) + suffix
        val newSelection = TextRange((prefix.length - 1).coerceAtLeast(0))
        textFieldValueState = TextFieldValue(text = newText, selection = newSelection)
        if (isTextValid.not()) {
            isTextValid = textFieldValueState.isValidDecimalSum()
        }
    }

    // Text field without keyboard, but selection
    CompositionLocalProvider(
        LocalTextInputService provides null
    ) {
        TextField(
            value = textFieldValueState,
            onValueChange = { tfv ->
                // If the text has not been altered, the user changed the selection.
                // In this case, we accept the user change of selection.
                if (tfv.text == textFieldValueState.text) {
                    textFieldValueState = textFieldValueState.copy(
                        selection = tfv.selection
                    )
                }
            },
            isError = isTextValid.not(),
            label = { Text(labelName) },
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .focusRequester(focusRequester)
        )
    }
    LaunchedEffect(key1 = null) {
        focusRequester.requestFocus()
    }


    // Grid of num pad and, plus, decimal point and okay button
    LazyHorizontalGrid(
        rows = GridCells.Fixed(4),
        contentPadding = PaddingValues(12.dp)
    ) {
        // Col #1
        item {
            DecimalInputButton(
                char = "+",
                enabled = textFieldValueState.isAllowingPlus(),
                onClick = ::insertChar
            )
        }
        item {
            DecimalInputButton("1", true, ::insertChar)
        }
        item {
            DecimalInputButton("4", true, ::insertChar)
        }
        item {
            DecimalInputButton("7", true, ::insertChar)
        }

        // Col #2
        item {
            DecimalInputButton("0", true, ::insertChar)
        }
        item {
            DecimalInputButton("2", true, ::insertChar)
        }
        item {
            DecimalInputButton("5", true, ::insertChar)
        }
        item {
            DecimalInputButton("8", true, ::insertChar)
        }

        // Col #3
        item {
            DecimalInputButton(
                char = ",",
                enabled = textFieldValueState.isAllowingComma(),
                onClick = ::insertChar
            )
        }
        item {
            DecimalInputButton("3", true, ::insertChar)
        }
        item {
            DecimalInputButton("6", true, ::insertChar)
        }
        item {
            DecimalInputButton("9", true, ::insertChar)
        }

        // Col #4
        item(span = { GridItemSpan(2) }) {
            InputButton(text = "⌫", onClick = ::delChar)
        }
        item(span = { GridItemSpan(2) }) {
            InputButton(text = "Okay") {
                isTextValid = textFieldValueState.isValidDecimalSum()
                if (isTextValid) onValidTextUpdated(textFieldValueState.text)
            }
        }
    }
}

@Composable
fun DecimalInputButton(char: String, enabled: Boolean = true, onClick: (String) -> Unit = {}) {
    InputButton(text = char, enabled = enabled) {
        onClick(char)
    }
}

@Composable
fun InputButton(text: String, enabled : Boolean = true, onClick: () -> Unit = {}) {
    TextButton(
        onClick =  onClick,
        enabled = enabled,
        modifier = Modifier.padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        Text(text = text, fontWeight = FontWeight.ExtraBold)
    }
}

fun TextFieldValue.isAllowingPlus(): Boolean {
    val before = this.getTextBeforeSelection(selection.min)
    return before.isNotEmpty() && before.last() !in listOf('+', ',')
}

fun TextFieldValue.isAllowingComma(): Boolean {
    val before = this.getTextBeforeSelection(selection.min)
    return before.isNotEmpty() && before.last() !in listOf('+', ',') && run {
        val lastPlusIndex = before.lastIndexOf('+')
        val lastCommaIndex = before.lastIndexOf(',')
        // If there is no comma or the last plus is after the last comma, we are allowing a comma
        return@run lastCommaIndex == -1 || lastPlusIndex > lastCommaIndex
    }
}

fun TextFieldValue.isValidDecimalSum(): Boolean {
    // Regex for a valid decimal sum
    // Constraints:
    // - Each summand may have one comma between digits
    // - The summands are separated by plus
    // Example: 0,0+5+12345,67
    val summand = """\d+(,\d+)?"""
    val regex = Regex("""^($summand[+])*$summand$""")
    return regex.matches(this.text)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun DecimalSummingFieldPreview() {
    AufmassManagerIsoBasaranTheme {
        Column {
            OutlinedTextField(
                value = "Text",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                label = {
                    Text("Label")
                }
            )
            DecimalSummingInput(
                field = DecimalSummingField(
                    name = "Decimal Summing Field",
                    initialValue = "0,0",
                )
            )
        }
    }
}