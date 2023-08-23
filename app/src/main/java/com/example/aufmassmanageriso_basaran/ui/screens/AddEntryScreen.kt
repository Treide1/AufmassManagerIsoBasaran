package com.example.aufmassmanageriso_basaran.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aufmassmanageriso_basaran.ui.state.MainViewModel
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

@Composable
fun AddEntryScreen(
    model: MainViewModel = MainViewModel(),
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Card(
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text="Eintrag hinzufügen")
            val context = LocalContext.current
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = model::fetchTestResult) {
                Text(text="Remote Fetch")
            }
            Button(onClick = model::createTestResult) {
                Text(text="Remote Create")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val testResult by model.testResult.observeAsState("")

                Text(text= "Result: $testResult")
            }

        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun AddEntryScreenPreview() {
    AufmassManagerIsoBasaranTheme {
        AddEntryScreen()
    }
}
