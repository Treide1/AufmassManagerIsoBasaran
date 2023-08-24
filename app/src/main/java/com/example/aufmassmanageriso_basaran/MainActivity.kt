package com.example.aufmassmanageriso_basaran

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aufmassmanageriso_basaran.ui.AufmassManagerApp
import com.example.aufmassmanageriso_basaran.ui.state.MainViewModel
import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AufmassManagerIsoBasaranTheme {
                val viewModel = viewModel<MainViewModel>()
                AufmassManagerApp(viewModel)
            }
        }
    }
}
