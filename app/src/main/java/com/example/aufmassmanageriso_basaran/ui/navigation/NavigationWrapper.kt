package com.example.aufmassmanageriso_basaran.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aufmassmanageriso_basaran.ui.state.MainViewModel
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme
import kotlinx.coroutines.launch

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val screen: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationWrapper(
    items: List<NavigationItem>,
    navHostController: NavHostController = rememberNavController(),
    model: MainViewModel = MainViewModel(),
    startItem: NavigationItem = items.first()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var screenTitle by remember { mutableStateOf(startItem.title) }

    // Drawer for app navigation
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        label = {
                            Text(text = item.title)
                        },
                        selected = navHostController.currentDestination?.route == item.route,
                        onClick = {
                            navHostController.navigate(item.route) {
                                // Pop up to the start destination of the graph, then select item
                                popUpTo(navHostController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }

                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        // Screen wrapping with scaffold (containing top app bar) of the navigable content
        val isSynced by model.isSyncedWithServer.collectAsState()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(screenTitle) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        if (isSynced.not()) {
                            Icon(imageVector = Icons.Default.SyncProblem, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                )
            },
        ) { padding ->
            Surface(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NavHost(
                    navController = navHostController,
                    startDestination = startItem.route
                ) {
                    items.forEach { item ->
                        composable(item.route) {
                            LaunchedEffect(item.route) { screenTitle = item.title }
                            item.screen()
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun NavigationWrapperPreview() {
    AufmassManagerIsoBasaranTheme {
        val items = listOf(
            NavigationItem(
                title = "Einstellungen",
                icon = Icons.Filled.Settings,
                route = "settings",
                screen = {
                    Text("settings screen")
                }
            ),
            NavigationItem(
                title = "Eintrag hinzufügen",
                icon = Icons.Filled.AddBox,
                route = "add_entry",
                screen = {
                    Text("entry screen")
                }
            )
        )
        NavigationWrapper(items = items)
    }
}