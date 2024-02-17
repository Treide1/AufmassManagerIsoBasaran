package com.example.aufmassmanageriso_basaran.ui.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.data.local.BauvorhabenForm
import com.example.aufmassmanageriso_basaran.data.local.TextFormField
import com.example.aufmassmanageriso_basaran.ui.components.ButtonRow
import com.example.aufmassmanageriso_basaran.ui.components.FormTextInput
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

@Composable
fun CreateBauvorhabenScreen(
    form: BauvorhabenForm = BauvorhabenForm(),
    createBauvorhaben: (form: BauvorhabenForm) -> Unit = { },
    onAbort: () -> Unit = {}
) {
    val fields by form.fields.collectAsState()

    // Input mask
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        //////////////////////////
        // Populating the form fields
        fields.forEach { field ->
            when (field) {
                is TextFormField -> {
                    FormTextInput(field)
                }
            }
        }
        //////////////////////////
        // Buttons: Abort, Create
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
                onClick = { createBauvorhaben(form) }
            ) {
                Text(text = "Erstellen")
            }
        }
        //////////////////////////
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SettingsScreenPreview() {
    AufmassManagerIsoBasaranTheme {
        CreateBauvorhabenScreen()
    }
}