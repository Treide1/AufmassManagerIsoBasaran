package com.example.aufmassmanageriso_basaran.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.data.local.BauvorhabenForm
import com.example.aufmassmanageriso_basaran.ui.components.FormFieldInput
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBauvorhabenScreen(
    form: BauvorhabenForm = BauvorhabenForm(),
    createBauvorhaben: (BauvorhabenForm) -> List<String> = { emptyList() },
    onAbort: () -> Unit = {}
) {
    val fields by form.fields.collectAsState()
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = WindowInsets.ime
    ) { padding ->
        // Input mask
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            //////////////////////////
            // Populating the form fields
            fields.forEach { (name, field) ->
                FormFieldInput(
                    name = name,
                    formField = field
                )
            }
            //////////////////////////

            // Submit button (inside box that fills width, to align it right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
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
                    onClick = {
                        val responses = createBauvorhaben(form)
                        responses.forEach { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(text = "Erstellen")
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SettingsScreenPreview() {
    AufmassManagerIsoBasaranTheme {
        CreateBauvorhabenScreen()
    }
}