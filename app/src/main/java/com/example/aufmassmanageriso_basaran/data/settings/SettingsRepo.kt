package com.example.aufmassmanageriso_basaran.data.settings

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.BauvorhabenDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SettingsRepo(
    private val dataStore: DataStore<Preferences>
) {

    private val TAG = "SettingsRepo"

    private object PreferencesKeys {
        // TODO: use correct type (maybe not all strings, nullable)
        val selectedBauvorhaben_bauvorhaben = stringPreferencesKey("selectedBauvorhaben_bauvorhaben")
        val selectedBauvorhaben_aufmassNummer = longPreferencesKey("selectedBauvorhaben_aufmassNummer")
        val selectedBauvorhaben_auftragsNummer = longPreferencesKey("selectedBauvorhaben_auftragsNummer")
        val selectedBauvorhaben_notiz = stringPreferencesKey("selectedBauvorhaben_notiz")
    }

    private fun Preferences.toUserSettingsDto(): SettingsDto {
        val preferences = this
        val dtoOrNull = run {
            BauvorhabenDto(
                name = preferences[PreferencesKeys.selectedBauvorhaben_bauvorhaben] ?: return@run null,
                aufmassNummer = preferences[PreferencesKeys.selectedBauvorhaben_aufmassNummer] ?: return@run null,
                auftragsNummer = preferences[PreferencesKeys.selectedBauvorhaben_auftragsNummer],
                notiz = preferences[PreferencesKeys.selectedBauvorhaben_notiz],
            )
        }
        return SettingsDto(selectedBauvorhaben = dtoOrNull)
    }

    /**
     * Update the selected bauvorhaben in the user preferences.
     */
    suspend fun updateSelectedBauvorhaben(dto: BauvorhabenDto) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.selectedBauvorhaben_bauvorhaben] = dto.name
            preferences[PreferencesKeys.selectedBauvorhaben_aufmassNummer] = dto.aufmassNummer
            preferences[PreferencesKeys.selectedBauvorhaben_auftragsNummer] = dto.auftragsNummer ?: 0
            preferences[PreferencesKeys.selectedBauvorhaben_notiz] = dto.notiz ?: ""
        }
    }

    /**
     * Get the user preferences flow.
     */
    val userPreferencesFlow: Flow<SettingsDto?> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences.toUserSettingsDto()
        }

}

