package com.example.aufmassmanageriso_basaran.ui.state

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
* Main view model holding the settings state and the entries state
 */
class MainViewModel: ViewModel() {
    private val _settingsState = mutableStateOf(SettingsState(aufmassDatum = getSystemDate()))
    fun getSettingsState() = _settingsState.value
    fun updateSettingsState(settingsState: SettingsState) {
        _settingsState.value = settingsState
    }

    private val _entriesState = mutableStateOf(EntriesState())
    fun getEntriesState() = _entriesState.value

    private fun getSystemDate(): String {
        val currentDateTime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return formatter.format(currentDateTime) //currentDateTime.format(formatter)
    }
}