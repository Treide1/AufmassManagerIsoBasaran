package com.example.aufmassmanageriso_basaran.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.BauvorhabenDto
import com.example.aufmassmanageriso_basaran.data.utility.logging.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class SettingsRepo(
    private val dataStore: DataStore<Preferences>
) {

    private val logger = Logger("SettingsRepo")

    private companion object {
        val selectedBauvorhaben_bauvorhaben = stringPreferencesKey("selectedBauvorhaben_bauvorhaben")
        val selectedBauvorhaben_aufmassNummer = longPreferencesKey("selectedBauvorhaben_aufmassNummer")
        val selectedBauvorhaben_auftragsNummer = longPreferencesKey("selectedBauvorhaben_auftragsNummer")
        val selectedBauvorhaben_notiz = stringPreferencesKey("selectedBauvorhaben_notiz")
        val selectedBauvorhaben__docId = stringPreferencesKey("selectedBauvorhaben__docId")
    }

    private fun Preferences.toSettingsDto(): SettingsDto {
        logger.d("Called toUserSettingsDto")
        val preferences = this
        val dtoOrNull = run {
            BauvorhabenDto(
                name = preferences[selectedBauvorhaben_bauvorhaben] ?: return@run null,
                aufmassNummer = preferences[selectedBauvorhaben_aufmassNummer] ?: return@run null,
                auftragsNummer = preferences[selectedBauvorhaben_auftragsNummer],
                notiz = preferences[selectedBauvorhaben_notiz],
                _docID = preferences[selectedBauvorhaben__docId],
            )
        }
        logger.d("toUserSettingsDto: dtoOrNull=$dtoOrNull")
        return SettingsDto(selectedBauvorhaben = dtoOrNull)
    }

    /**
     * Update the selected bauvorhaben in the user preferences.
     */
    suspend fun updateSelectedBauvorhaben(dto: BauvorhabenDto) {
        logger.d("updateSelectedBauvorhaben: $dto")
        dataStore.edit { preferences ->
            preferences[selectedBauvorhaben_bauvorhaben] = dto.name
            preferences[selectedBauvorhaben_aufmassNummer] = dto.aufmassNummer
            dto.auftragsNummer?.let { preferences[selectedBauvorhaben_auftragsNummer] =  it }
            dto.notiz?.let { preferences[selectedBauvorhaben_notiz] = it }
            dto._docID?.let { preferences[selectedBauvorhaben__docId] = it }
        }
        dataStore.data.first().let { logger.d("updateSelectedBauvorhaben: dataStore.data=$it") }
    }

    /**
     * Remove the selected bauvorhaben from the user preferences.
     */
    suspend fun removeSelectedBauvorhaben() {
        logger.d("removeSelectedBauvorhaben")
        dataStore.edit { preferences ->
            preferences.remove(selectedBauvorhaben_bauvorhaben)
            preferences.remove(selectedBauvorhaben_aufmassNummer)
            preferences.remove(selectedBauvorhaben_auftragsNummer)
            preferences.remove(selectedBauvorhaben_notiz)
            preferences.remove(selectedBauvorhaben__docId)
        }
    }

    /**
     * Get the user preferences flow.
     */
    val userPreferencesFlow: Flow<SettingsDto?> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                logger.i("Reading data resulted in IOException. Emitting emptyPreferences instead.")
                emit(emptyPreferences())
            } else {
                logger.e("Error reading preferences.", exception)
                throw exception
            }
        }.map { preferences ->
            logger.d("userPreferencesFlow: preferences=$preferences")
            preferences.toSettingsDto()
        }

}

