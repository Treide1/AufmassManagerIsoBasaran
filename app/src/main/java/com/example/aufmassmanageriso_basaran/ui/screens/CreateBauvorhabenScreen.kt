package com.example.aufmassmanageriso_basaran.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.data.remote.BauvorhabenDto
import com.example.aufmassmanageriso_basaran.ui.components.ValidatedInput
import com.example.aufmassmanageriso_basaran.ui.state.MainViewModel
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBauvorhabenScreen(
    model: MainViewModel = MainViewModel()
) {
    var bauvorhabenData by remember { mutableStateOf("") }
    var aufmassNummerData by remember { mutableStateOf("") }
    var auftragsNummerData by remember { mutableStateOf("") }
    var notizData by remember { mutableStateOf("") }

    val isFormComplete by remember {
        derivedStateOf { bauvorhabenData.isNotBlank() && aufmassNummerData.isNotBlank() }
    }

    Scaffold(
        floatingActionButton = {
            val context = LocalContext.current
            FloatingActionButton(
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                onClick = {
                    if (isFormComplete) {
                        val dto = BauvorhabenDto(
                            bauvorhaben = bauvorhabenData,
                            aufmassNummer = aufmassNummerData.toLong(),
                            auftragsNummer = auftragsNummerData.toLongOrNull(),
                            notiz = notizData.ifBlank { null }
                        )
                        model.createBauvorhaben(dto)
                    } else {
                        Toast.makeText(
                            context,
                            "Bitte fülle alle Pflichtfelder aus.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        // Input mask
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Bauvorhaben

            ValidatedInput(
                labelText = "Bauvorhaben",
                isRequired = true,
                keyboardType = KeyboardType.Text,
                getDefaultValue = { bauvorhabenData },
                isValidOnConfirm = { it.isNotEmpty() },
                onValid = { bauvorhabenData = it }
            )

            // Aufmassnummer
            ValidatedInput(
                labelText = "Aufmass-Nummer",
                isRequired = true,
                keyboardType = KeyboardType.Number,
                getDefaultValue = { aufmassNummerData },
                cleanInput = { it.replace(Regex("[^0-9]"), "") },
                isValidOnConfirm = {
                    val updated = it.toIntOrNull()
                    updated != null && updated > 0
               },
                onValid = { aufmassNummerData = it }
            )

            // Auftragsnummer
            ValidatedInput(
                labelText = "Auftrags-Nummer",
                isRequired = false,
                keyboardType = KeyboardType.Number,
                getDefaultValue = { auftragsNummerData },
                cleanInput = { it.replace(Regex("[^0-9]"), "") },
                isValidOnConfirm = {
                    val updated = it.toIntOrNull()
                    updated != null && updated > 0
                },
                onValid = { auftragsNummerData = it }
            )

            // Notiz
            OutlinedTextField(
                value = notizData,
                onValueChange = {
                    notizData = it
                },
                label = { Text("Notiz") },
                supportingText = { Text("Anmerkungen zum Bauvorhaben") },
            )
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