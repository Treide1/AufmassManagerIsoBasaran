package com.example.aufmassmanageriso_basaran.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aufmassmanageriso_basaran.ui.state.MainViewModel
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

@Composable
fun AddEntryScreen(
    model: MainViewModel = MainViewModel()
) {
    Text(text = "AddEntryScreen (TODO)")
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun AddEntryScreenPreview() {
    AufmassManagerIsoBasaranTheme {
        AddEntryScreen()
    }
}
