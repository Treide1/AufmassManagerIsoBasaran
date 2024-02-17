package com.example.aufmassmanageriso_basaran.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.data.local.DecimalSummingField
import com.example.aufmassmanageriso_basaran.data.local.DerivedFormField
import com.example.aufmassmanageriso_basaran.data.local.EintragForm
import com.example.aufmassmanageriso_basaran.data.local.TextFormField
import com.example.aufmassmanageriso_basaran.ui.components.ButtonRow
import com.example.aufmassmanageriso_basaran.ui.components.DecimalSummingInput
import com.example.aufmassmanageriso_basaran.ui.components.DerivedTextLabel
import com.example.aufmassmanageriso_basaran.ui.components.FormTextInput
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

@Composable
fun CreateEintragScreen(
    form: EintragForm,
    bauvorhabenName: String,
    createEintrag: (form: EintragForm, bauvorhabenName: String) -> Unit = { _, _ -> },
    onAbort: () -> Unit = {}
) {
    val fields by form.fields.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        //////////////////////////
        Box(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("Bauvorhaben:")
                    }
                    append(" $bauvorhabenName")
                },
            )

        }
        //////////////////////////
        // Populating the form fields
        fields.forEach { field ->
            when (field) {
                is TextFormField -> {
                    FormTextInput(textFormField = field)
                }
                is DerivedFormField -> {
                    DerivedTextLabel(derivedFormField = field)
                }
                is DecimalSummingField -> {
                    DecimalSummingInput(field = field)
                }
            }
        }
        //////////////////////////
        ButtonRow {
            // Cancel
            OutlinedButton(
                onClick = onAbort,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(text = "Abbrechen")
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Create
            OutlinedButton(
                onClick = { createEintrag(form, bauvorhabenName) }
            ) {
                Text(text = "Erstellen")
            }
        }
        //////////////////////////
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun AddEntryScreenPreview() {
    AufmassManagerIsoBasaranTheme {
        CreateEintragScreen(EintragForm(), "Bauvorhaben 1")
    }
}
