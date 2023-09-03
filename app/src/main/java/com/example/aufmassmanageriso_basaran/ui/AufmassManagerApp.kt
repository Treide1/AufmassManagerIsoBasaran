package com.example.aufmassmanageriso_basaran.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.example.aufmassmanageriso_basaran.ui.navigation.NavigationItem
import com.example.aufmassmanageriso_basaran.ui.navigation.NavigationWrapper
import com.example.aufmassmanageriso_basaran.ui.screens.AddEntryScreen
import com.example.aufmassmanageriso_basaran.ui.screens.CreateBauvorhabenScreen
import com.example.aufmassmanageriso_basaran.ui.screens.SelectBauvorhabenScreen
import com.example.aufmassmanageriso_basaran.ui.state.MainViewModel
import kotlinx.coroutines.flow.first

/**
 * Main entry point for the app. Contains a [NavigationWrapper] with the different screens.
 *
 * This serves as the root composable for the app. Here, dependencies are passed to the different
 * screens.
 */
@Composable
fun AufmassManagerApp(
    model: MainViewModel,
    navHostController: NavHostController
) {
    val isSynced by model.isSyncedWithServer.collectAsState()

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
                    form = model.bauvorhabenForm,
                    createBauvorhaben = model::createBauvorhaben,
                    onAbort = { navHostController.navigateUp() }
                ) }
            ),
            NavigationItem(
                title = "Bauvorhaben auswählen",
                icon = Icons.Filled.Construction,
                route = "bauvorhaben_select",
                screen = {
                    val searchText by model.searchText.collectAsState()
                    val searchResults by model.searchResults.collectAsState()
                    val isSearching by model.isSearching.collectAsState()

                    val selectedBauvorhaben by model.selectedBauvorhaben.collectAsState()

                    // Fetch data when opening screen
                    LaunchedEffect(Unit) {
                        // TODO: introduce TTL logic to invalidate cache (currently just invalid at start)
                        if(model.allBauvorhaben.first().isEmpty()) model.fetchAllBauvorhaben()
                    }

                    SelectBauvorhabenScreen(
                        searchText = searchText,
                        searchResults = searchResults,
                        isSearching = isSearching,
                        onSearchTextChange = model::onSearchTextChange,
                        selectedBauvorhaben = selectedBauvorhaben,
                        selectBauvorhaben = model::selectBauvorhaben
                    )
                }
            ),
            NavigationItem(
                title = "Eintrag hinzufügen",
                icon = Icons.Filled.AddBox,
                route = "add_entry",
                screen = { AddEntryScreen() }
            )
        ),
        navHostController = navHostController,
        actionImages = mutableListOf<ImageVector>().also { list ->
            if (isSynced.not()) list.add(Icons.Default.SyncProblem)
        }
    )
}
