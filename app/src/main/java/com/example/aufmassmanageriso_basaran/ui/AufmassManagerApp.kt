package com.example.aufmassmanageriso_basaran.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aufmassmanageriso_basaran.ui.navigation.NavigationItem
import com.example.aufmassmanageriso_basaran.ui.navigation.NavigationWrapper
import com.example.aufmassmanageriso_basaran.ui.screens.SettingsScreen
import com.example.aufmassmanageriso_basaran.ui.state.MainViewModel
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

@Composable
fun AufmassManagerApp(mainViewModel: MainViewModel = MainViewModel()) {
    NavigationWrapper(
        items = listOf(
            NavigationItem(
                title = "Einstellungen",
                icon = Icons.Filled.Settings,
                route = "settings",
                screen = { modifier ->
                    SettingsScreen(model = mainViewModel, modifier = modifier)
                }
            ),
            NavigationItem(
                title = "Eintrag hinzufügen",
                icon = Icons.Filled.AddBox,
                route = "add_entry",
                screen = {  modifier ->
                    Text("entry screen", modifier = modifier)
                }
            )
        )
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PortraitAppPreview() {
    AufmassManagerIsoBasaranTheme {
        AufmassManagerApp()
    }
}