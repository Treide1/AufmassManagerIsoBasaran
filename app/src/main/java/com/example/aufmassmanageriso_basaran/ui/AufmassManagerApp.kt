package com.example.aufmassmanageriso_basaran.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.aufmassmanageriso_basaran.ui.navigation.NavigationItem
import com.example.aufmassmanageriso_basaran.ui.navigation.NavigationWrapper
import com.example.aufmassmanageriso_basaran.ui.screens.AddEntryScreen
import com.example.aufmassmanageriso_basaran.ui.screens.CreateBauvorhabenScreen
import com.example.aufmassmanageriso_basaran.ui.screens.SelectBauvorhabenScreen
import com.example.aufmassmanageriso_basaran.ui.state.MainViewModel
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

/**
 * Main entry point for the app. Contains a [NavigationWrapper] with the different screens.
 */
@Composable
fun AufmassManagerApp(
    mainViewModel: MainViewModel = MainViewModel(),
    navHostController: NavHostController = rememberNavController()
) {
    NavigationWrapper(
        items = listOf(
            NavigationItem(
                title = "Informationen",
                icon = Icons.Filled.Info,
                route = "info",
                screen = { Text(text = "Allgemeine Informationen über die App +\n aktuelle Informationen (z.B. Probleme)") }
            ),
            NavigationItem(
                title = "Bauvorhaben hinzufügen",
                icon = Icons.Filled.LibraryAdd,
                route = "bauvorhaben_add",
                screen = { CreateBauvorhabenScreen(
                    model = mainViewModel,
                    onAbort = { navHostController.navigateUp() }
                ) }
            ),
            NavigationItem(
                title = "Bauvorhaben auswählen",
                icon = Icons.Filled.Construction,
                route = "bauvorhaben_select",
                screen = { SelectBauvorhabenScreen(model = mainViewModel) }
            ),
            NavigationItem(
                title = "Eintrag hinzufügen",
                icon = Icons.Filled.AddBox,
                route = "add_entry",
                screen = { AddEntryScreen(model = mainViewModel) }
            )
        ),
        navHostController = navHostController,
        model = mainViewModel
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PortraitAppPreview() {
    AufmassManagerIsoBasaranTheme {
        AufmassManagerApp()
    }
}