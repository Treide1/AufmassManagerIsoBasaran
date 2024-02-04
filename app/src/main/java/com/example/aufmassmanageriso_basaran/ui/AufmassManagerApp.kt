package com.example.aufmassmanageriso_basaran.ui

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.DomainAdd
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.aufmassmanageriso_basaran.presentation.MainViewModel
import com.example.aufmassmanageriso_basaran.ui.navigation.NavigationItem
import com.example.aufmassmanageriso_basaran.ui.navigation.NavigationWrapper
import com.example.aufmassmanageriso_basaran.ui.screens.BauvorhabenHinzufuegenScreen
import com.example.aufmassmanageriso_basaran.ui.screens.EintragHinzufuegenScreen
import com.example.aufmassmanageriso_basaran.ui.screens.SpezialHinzufuegenScreen
import com.example.aufmassmanageriso_basaran.ui.screens.InformationenScreen
import com.example.aufmassmanageriso_basaran.ui.screens.BauvorhabenAuswaehlenScreen

/**
 * Main entry point for the app. Contains a [NavigationWrapper] with the different screens.
 *
 * This serves as the root composable for the app. Here, dependencies are injected into the
 * screens.
 */
@Composable
fun AufmassManagerApp(
    model: MainViewModel,
    navHostController: NavHostController
) {
    val isSynced by model.isSyncedWithServer.collectAsState()
    val selectedBauvorhaben by model.selectedBauvorhaben.collectAsState()
    val mainScreenRoute = "informationen"

    // Fetch data when app is started
    LaunchedEffect(Unit) {
        model.fetchBauvorhabenNames()
    }

    NavigationWrapper(
        items = listOf(
            NavigationItem(
                title = "Informationen",
                icon = Icons.Filled.Info,
                route = mainScreenRoute,
                screen = {
                    InformationenScreen()
                }
            ),
            NavigationItem(
                title = "Bauvorhaben hinzufügen",
                icon = Icons.Filled.DomainAdd,
                route = "bauvorhaben_add",
                screen = {
                    BauvorhabenHinzufuegenScreen(
                        form = model.bauvorhabenForm,
                        createBauvorhaben = model::createBauvorhaben,
                        onAbort = {
                            // Clear form fields and navigate back
                            model.bauvorhabenForm.clearFields()
                            navHostController.navigate(mainScreenRoute)
                        }
                    )
                }
            ),
            NavigationItem(
                title = "Bauvorhaben auswählen",
                icon = Icons.Filled.Domain,
                route = "bauvorhaben_select",
                screen = {
                    val searchText by model.searchText.collectAsState()
                    val searchResults by model.searchResults.collectAsState()
                    val isSearching by model.isSearching.collectAsState()

                    // Fetch data when opening screen
                    LaunchedEffect(Unit) {
                        model.onOpenSelectBauvorhabenScreen()
                    }

                    BauvorhabenAuswaehlenScreen(
                        searchText = searchText,
                        searchResults = searchResults,
                        isSearching = isSearching,
                        onSearchTextChange = model::onSearchTextChange,
                        selectedBauvorhaben = selectedBauvorhaben,
                        selectBauvorhabenByName = model::selectBauvorhaben
                    )
                }
            ),
            NavigationItem(
                title = "Eintrag hinzufügen",
                icon = Icons.Filled.AddBox,
                route = "add_entry",
                screen = {
                    if (selectedBauvorhaben == null) {
                        // Navigate back if no bauvorhaben is selected
                        navHostController.navigate(mainScreenRoute)
                        Toast.makeText(LocalContext.current, "Kein Bauvorhaben ausgewählt", Toast.LENGTH_SHORT).show()
                    } else {
                        EintragHinzufuegenScreen(
                            form = model.eintragForm,
                            bauvorhabenName = selectedBauvorhaben!!.name,
                            createEintrag = model::createEintrag,
                            onAbort = {
                                // Clear form fields and navigate back
                                model.eintragForm.clearFields()
                                navHostController.navigate(mainScreenRoute)
                            }
                        )
                    }

                }
            ),
            NavigationItem(
                title = "Spezial-Eintrag hinzufügen",
                icon = Icons.Filled.NoteAdd,
                route = "add_special_entry",
                screen = {
                    if (selectedBauvorhaben == null) {
                        // Navigate back if no bauvorhaben is selected
                        navHostController.navigate(mainScreenRoute)
                        Toast.makeText(LocalContext.current, "Kein Bauvorhaben ausgewählt", Toast.LENGTH_SHORT).show()
                    } else {
                        SpezialHinzufuegenScreen(
                            form = model.spezialForm,
                            bauvorhabenName = selectedBauvorhaben!!.name,
                            createSpezial = model::createSpezial
                        ) {
                            // Clear form fields and navigate back
                            model.spezialForm.clearFields()
                            navHostController.navigate(mainScreenRoute)
                        }
                    }

                }
            ),
            NavigationItem(
                title = "Backup herunterladen",
                icon = Icons.Filled.Download,
                route = "download_backup",
                screen = {
                    LaunchedEffect(key1 = null) {
                        model.downloadBackup()
                        navHostController.navigate(mainScreenRoute)
                    }
                }
            )
        ),
        navHostController = navHostController,
        actionImages = mutableListOf<ImageVector>().also { list ->
            if (isSynced.not()) list.add(Icons.Default.SyncProblem)
        }
    )
}
