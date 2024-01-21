package com.example.aufmassmanageriso_basaran.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aufmassmanageriso_basaran.data.local.BauvorhabenForm
import com.example.aufmassmanageriso_basaran.data.local.EintragForm
import com.example.aufmassmanageriso_basaran.data.mapping.toDto
import com.example.aufmassmanageriso_basaran.data.remote.FirestoreRepo
import com.example.aufmassmanageriso_basaran.data.settings.SettingsRepo
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
* Main view model holding the state values and performs state logic of the app.
 */
@Suppress("MemberVisibilityCanBePrivate")
class MainViewModel(
    private val settingsRepo: SettingsRepo,
    private val displayMsgToUser: (msg: String) -> Unit,
): ViewModel() {

    companion object {
        /**
         * Tag for logging.
         */
        private const val TAG = "MainViewModel"
    }

    /////////////////////////// CONNECTIVITY //////////////////////////////////

    /**
     * State flow indicating whether the Firestore instance is synced with the server.
     */
    val isSyncedWithServer: StateFlow<Boolean> = FirestoreRepo.isSyncedWithServer

    ///////////////////////////  FORM DATA   //////////////////////////////////

    // Create Bauvorhaben
    val bauvorhabenForm = BauvorhabenForm()

    fun createBauvorhaben(form: BauvorhabenForm) {
        Log.d(TAG, "createBauvorhaben: Creating. form=$form")
        if (form.validate().not()) {
            Log.d(TAG, "createBauvorhaben: Validation failed. form=$form")
            displayMsgToUser("Bitte fülle alle Pflichtfelder aus.")
            return
        }
        Log.d(TAG, "createBauvorhaben: Validation success. form=$form")

        FirestoreRepo.createBauvorhabenDoc(form.toDto()) { isSuccess ->
            Log.d(TAG, "createBauvorhaben: isSuccess=$isSuccess")
            if (isSuccess.not()) {
                Log.e(TAG, "createBauvorhaben: Could not create bauvorhaben.")
                displayMsgToUser("Fehler beim Erstellen des Bauvorhabens.")
                return@createBauvorhabenDoc
            }
            Log.d(TAG, "createBauvorhaben: Created. form=$form")
            displayMsgToUser("Bauvorhaben wurde erstellt.")

            form.clearFields()
        }
    }

    /////////////////////////////////////////////////////////////

    // Select Bauvorhaben
    private val _bauvorhabenNames = MutableStateFlow<List<String>>(emptyList())
    val bauvorhabenNames = _bauvorhabenNames.asStateFlow()

    val selectedBauvorhaben = settingsRepo.userPreferencesFlow
        .map { settings ->
            settings?.selectedBauvorhaben
        }
        .onEach { selectedBauvorhaben ->
            Log.d(TAG, "selectBauvorhaben: selectedBauvorhaben=$selectedBauvorhaben")
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    @Suppress("UNCHECKED_CAST") // Justification: We know the type of the document.
    fun fetchBauvorhabenNames() {
        Log.d(TAG, "fetchBauvorhabenNames: Fetching...")
        FirestoreRepo.getMetaBauvorhabenDoc(
            onSuccess = { doc ->
                val projection = doc.get("projection_name") as List<String>
                _bauvorhabenNames.update { projection }
            },
            onFailure = { e ->
                Log.e(TAG, "fetchBauvorhabenNames: Could not fetch bauvorhabenNames.", e)
                displayMsgToUser("Fehler: "+ (e.message ?: "Keine Fehler-Nachricht erhalten."))
            }
        )
    }

    fun selectBauvorhaben(bauvorhabenName: String) {
        // TODO: Perform as transaction. If failed, invalidate cache.
        Log.d(TAG, "selectBauvorhaben: Selecting. bauvorhabenName=$bauvorhabenName")

        // Fetch bauvorhabenDto
        FirestoreRepo.getBauvorhabenByName(bauvorhabenName) { task ->
            viewModelScope.launch {
                Log.d(TAG, "selectBauvorhaben: getBauvorhabenDoc.isSuccessful=${task.isSuccessful}")
                val docs = task.result.documents
                // If 0 or 2+ bauvorhaben with same name exist, log error and return.
                if (docs.size != 1) {
                    Log.e(TAG, "selectBauvorhaben: Found n=${docs.size} bauvorhaben with name $bauvorhabenName.")
                    displayMsgToUser("Fehler beim Auswählen des Bauvorhabens.")
                    return@launch
                }

                // Update selected bauvorhaben in user preferences
                val dto = docs.first().toDto()
                settingsRepo.updateSelectedBauvorhaben(dto)
            }
        }
        
    }

    suspend fun onOpenSelectBauvorhabenScreen() {
        if (bauvorhabenNames.first().isEmpty()) fetchBauvorhabenNames()
    }

    /////////////////////////////////////////////////////////////

    // Create Eintrag
    val eintragForm = EintragForm()

    fun createEintrag(form: EintragForm, bauvorhabenName: String) {
        Log.d(TAG, "createEintrag: Creating. form=$form, bauvorhabenName=$bauvorhabenName")

        if (form.validate().not()) {
            Log.d(TAG, "createEintrag: Validation failed. form=$form")
            displayMsgToUser("Bitte fülle alle Pflichtfelder aus.")
        }
        Log.d(TAG, "createEintrag: Validation success. form=$form")

        FirestoreRepo.createEintragDoc(form.toDto(), bauvorhabenName) { isSuccess ->
            Log.d(TAG, "createEintrag: isSuccess=$isSuccess")
            if (isSuccess.not()) {
                Log.e(TAG, "createEintrag: Could not create eintrag.")
                displayMsgToUser("Fehler beim Erstellen des Eintrags.")
                return@createEintragDoc
            }
            Log.d(TAG, "createEintrag: Created. form=$form, ")
            displayMsgToUser("Eintrag wurde erstellt.")

            form.clearFields()
        }
    }

    ///////////////////////////   SEARCH   //////////////////////////////////

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    /**
     * Search results are the computed using the search text and the list of all bauvorhaben.
     */
    @OptIn(FlowPreview::class)
    val searchResults = searchText
        // On change, assume user is searching.
        .onEach { _isSearching.update { true } }
        // Debounce to prevent too many queries. If text doesn't change for 450ms, perform query.
        .debounce(450L)
        // Combine search text and bauvorhaben names to get search results.
        .combine(_bauvorhabenNames) { text, bauvorhabenList ->
            if (text.isBlank()) {
                bauvorhabenList
            } else {
                bauvorhabenList.filter { bauvorhaben ->
                    bauvorhaben.contains(text, ignoreCase = true)
                }
            }
        }
        // On filter result, assume user is done searching.
        .onEach { _isSearching.update { false } }
        // Cache the result. (Idiomatic way to cache a flow, do not change !)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _bauvorhabenNames.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

}


class MainViewModelFactory(
    private val settingsRepo: SettingsRepo,
    private val displayMsgToUser: (msg: String) -> Unit,
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST") // Justification: We know the type of the view model.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(settingsRepo, displayMsgToUser) as T
    }
}