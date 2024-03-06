package com.example.aufmassmanageriso_basaran.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.ui.components.ButtonRow
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme


@Composable
fun ExportBauvorhabenScreen(
    onExport: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ButtonRow {
            TextButton(
                onClick = {
                    // TODO: Mithilfe von HssfWorkbook exportieren
                    onExport()
                }
            ) {
                Text(text = "Bauvorhaben exportieren")
            }
        }
    }
}

@Preview
@Composable
fun ExportBauvorhabenScreenPreview() {
    AufmassManagerIsoBasaranTheme {
        ExportBauvorhabenScreen()
    }
}