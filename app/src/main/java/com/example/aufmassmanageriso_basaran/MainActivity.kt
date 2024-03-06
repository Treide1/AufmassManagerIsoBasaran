package com.example.aufmassmanageriso_basaran

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.aufmassmanageriso_basaran.data.remote.FirestoreRepo
import com.example.aufmassmanageriso_basaran.data.settings.SettingsRepo
import com.example.aufmassmanageriso_basaran.data.zip.FileRepo
import com.example.aufmassmanageriso_basaran.logging.Logger
import com.example.aufmassmanageriso_basaran.presentation.MainViewModel
import com.example.aufmassmanageriso_basaran.presentation.MainViewModelFactory
import com.example.aufmassmanageriso_basaran.ui.AufmassManagerApp
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme


private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class MainActivity : ComponentActivity() {

    val logger = Logger("MainActivityLogger")

    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            logger.e("NetworkCallback: The default network is now: $network")
        }

        override fun onLost(network: Network) {
            logger.e("NetworkCallback: The application no longer has a default network. The last default network was $network")
        }

        override fun onUnavailable() {
            logger.e("NetworkCallback: The application no longer has a default network.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.init(filesDir)
        FileRepo.init(this)

        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        FirestoreRepo.startConnectionListener()

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

    override fun onDestroy() {
        FirestoreRepo.stopConnectionListener()

        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.unregisterNetworkCallback(networkCallback)

        super.onDestroy()
    }
}
