package com.example.aufmassmanageriso_basaran.ui.state

import android.content.Context
import android.icu.util.Calendar
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aufmassmanageriso_basaran.data.remote.FirestoreDb
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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
        return formatter.format(currentDateTime)
    }

    val _testResult = MutableLiveData<String>()
    val testResult: LiveData<String> = _testResult

    fun fetchTestResult() {
        this.viewModelScope.launch {
            _testResult.value = try {
                FirestoreDb.getAllgemein()
            } catch (e: Exception) {
                e.stackTraceToString()
            }
        }
    }

    fun createTestResult() {
        this.viewModelScope.launch {
            _testResult.value = try {
                FirestoreDb.createAllgemein("BeispielBauvorhaben")
            } catch (e: Exception) {
                e.stackTraceToString()
            }
        }
    }

}