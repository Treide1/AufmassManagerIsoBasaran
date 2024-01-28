package com.example.aufmassmanageriso_basaran.ui

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.aufmassmanageriso_basaran.presentation.MainViewModel
import com.example.aufmassmanageriso_basaran.ui.navigation.NavigationItem
import com.example.aufmassmanageriso_basaran.ui.navigation.NavigationWrapper
import com.example.aufmassmanageriso_basaran.ui.screens.CreateBauvorhabenScreen
import com.example.aufmassmanageriso_basaran.ui.screens.CreateEintragScreen
import com.example.aufmassmanageriso_basaran.ui.screens.CreateSpezialScreen
import com.example.aufmassmanageriso_basaran.ui.screens.InformationenScreen
import com.example.aufmassmanageriso_basaran.ui.screens.SelectBauvorhabenScreen

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
    val home = "home"

    NavigationWrapper(
        items = listOf(
            NavigationItem(
                title = "Informationen",
                icon = Icons.Filled.Info,
                route = home,
                screen = {
                    InformationenScreen()
                }
            ),
            NavigationItem(
                title = "Bauvorhaben hinzufügen",
                icon = Icons.Filled.LibraryAdd,
                route = "bauvorhaben_add",
                screen = {
                    CreateBauvorhabenScreen(
                        form = model.bauvorhabenForm,
                        createBauvorhaben = model::createBauvorhaben,
                        onAbort = {
                            // Clear form fields and navigate back
                            model.bauvorhabenForm.clearFields()
                            navHostController.navigate(home)
                        }
                    )
                }
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
                        model.onOpenSelectBauvorhabenScreen()
                    }

                    SelectBauvorhabenScreen(
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
                    val selectedBauvorhaben by model.selectedBauvorhaben.collectAsState()

                    if (selectedBauvorhaben == null) {
                        // Navigate back if no bauvorhaben is selected
                        navHostController.navigate(home)
                        Toast.makeText(LocalContext.current, "Kein Bauvorhaben ausgewählt", Toast.LENGTH_SHORT).show()
                    } else {
                        CreateEintragScreen(
                            coroutineContext = model.viewModelScope.coroutineContext,
                            form = model.eintragForm,
                            bauvorhabenName = selectedBauvorhaben!!.name,
                            createEintrag = model::createEintrag,
                            onAbort = {
                                // Clear form fields and navigate back
                                model.eintragForm.clearFields()
                                navHostController.navigate(home)
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
                    val selectedBauvorhaben by model.selectedBauvorhaben.collectAsState()

                    if (selectedBauvorhaben == null) {
                        // Navigate back if no bauvorhaben is selected
                        navHostController.navigate(home)
                        Toast.makeText(LocalContext.current, "Kein Bauvorhaben ausgewählt", Toast.LENGTH_SHORT).show()
                    } else {
                        CreateSpezialScreen(
                            coroutineContext = model.viewModelScope.coroutineContext,
                            form = model.spezialForm,
                            bauvorhabenName = selectedBauvorhaben!!.name,
                            createSpezial = model::createSpezial,
                            onAbort = {
                                // Clear form fields and navigate back
                                model.spezialForm.clearFields()
                                navHostController.navigate(home)
                            }
                        )
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
