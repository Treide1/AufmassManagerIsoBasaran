package com.example.aufmassmanageriso_basaran

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.aufmassmanageriso_basaran.ui.AufmassManagerApp
import com.example.aufmassmanageriso_basaran.ui.state.MainViewModel

import com.example.aufmassmanageriso_basaran.ui.theme.AufmassManagerIsoBasaranTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainViewModel: MainViewModel by viewModels()

        setContent {
            AufmassManagerIsoBasaranTheme {
                AufmassManagerApp(mainViewModel)
            }
        }
    }
}
