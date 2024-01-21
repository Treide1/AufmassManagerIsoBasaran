package com.example.aufmassmanageriso_basaran

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.aufmassmanageriso_basaran.data.settings.SettingsRepo
import com.example.aufmassmanageriso_basaran.ui.AufmassManagerApp
import com.example.aufmassmanageriso_basaran.presentation.MainViewModel
import com.example.aufmassmanageriso_basaran.presentation.MainViewModelFactory
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsRepo = SettingsRepo(dataStore = dataStore)
        setContent {
            AufmassManagerIsoBasaranTheme {
                val viewModel = viewModel<MainViewModel>(
                    factory = MainViewModelFactory(settingsRepo) { msg ->
                        // Display message to user
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    }
                )
                val navController = rememberNavController()
                AufmassManagerApp(viewModel, navController)
            }
        }
    }
}
