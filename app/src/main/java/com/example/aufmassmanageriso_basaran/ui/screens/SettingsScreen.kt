package com.example.aufmassmanageriso_basaran.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeAction.Companion
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.ui.components.AddInputButton
import com.example.aufmassmanageriso_basaran.ui.components.ValidatedInput
import com.example.aufmassmanageriso_basaran.ui.state.MainViewModel
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    model: MainViewModel = MainViewModel(),
    modifier: Modifier = Modifier
) {
    val state = model.getSettingsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        // Input mask
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = null)
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = "Einstellungen",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Bauvorhaben
            ValidatedInput(
                labelText = "Bauvorhaben",
                isRequired = true,
                keyboardType = KeyboardType.Text,
                getDefaultValue = { state.bauvorhaben },
                isValidOnConfirm = { it.isNotEmpty() },
                onValid = { model.updateSettingsState(state.copy(bauvorhaben = it)) }
            )

            // Aufmassnummer
            ValidatedInput(
                labelText = "Aufmass-Nummer",
                isRequired = true,
                keyboardType = KeyboardType.Number,
                getDefaultValue = { state.aufmassNummer?.toString() ?: "" },
                cleanInput = { it.replace(Regex("[^0-9]"), "") },
                isValidOnConfirm = {
                    val updated = it.toIntOrNull()
                    updated != null && updated > 0
               },
                onValid = { model.updateSettingsState(state.copy(aufmassNummer = it.toInt())) }
            )

            // Auftragsnummer
            ValidatedInput(
                labelText = "Auftrags-Nummer",
                isRequired = false,
                keyboardType = KeyboardType.Number,
                getDefaultValue = { state.auftragsNummer?.toString() ?: "" },
                cleanInput = { it.replace(Regex("[^0-9]"), "") },
                isValidOnConfirm = {
                    val updated = it.toIntOrNull()
                    updated != null && updated > 0
                },
                onValid = { model.updateSettingsState(state.copy(auftragsNummer = it.toInt())) }
            )

            // Datum (wird vom System gesetzt, kann nicht geändert werdern)
            OutlinedTextField(
                value = state.aufmassDatum,
                onValueChange = {},
                modifier = Modifier
                    .padding(bottom = 16.dp),
                label = { Text("Datum") },
                readOnly = true,
                enabled = false
            )

            val noticeFieldAdded = remember { mutableStateOf(state.allgemeineNotiz.isNotBlank()) }
            var noticeFieldText by remember { mutableStateOf(state.allgemeineNotiz) }
            AddInputButton(
                isAdded = noticeFieldAdded,
                addOptionLabel = "Allgemeine Notiz",
            ) { requestFocusModifier ->
                OutlinedTextField(
                    value = noticeFieldText,
                    onValueChange = {
                        noticeFieldText = it
                    },
                    modifier = requestFocusModifier
                        .padding(bottom = 16.dp)
                        .height(64.dp)
                        .fillMaxWidth(),
                    label = { Text("Allgemeine Notiz") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (noticeFieldText.isBlank()) {
                                noticeFieldAdded.value = false
                            }
                            model.updateSettingsState(state.copy(allgemeineNotiz = noticeFieldText))
                            this.defaultKeyboardAction(ImeAction.Done)
                        }
                    )
                )
            }

        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SettingsScreenPreview() {
    AufmassManagerIsoBasaranTheme {
        SettingsScreen()
    }
}