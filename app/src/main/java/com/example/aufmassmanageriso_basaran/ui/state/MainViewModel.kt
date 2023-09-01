package com.example.aufmassmanageriso_basaran.ui.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aufmassmanageriso_basaran.data.local.BauvorhabenForm
import com.example.aufmassmanageriso_basaran.data.mapping.toDto
import com.example.aufmassmanageriso_basaran.data.remote.BauvorhabenDto
import com.example.aufmassmanageriso_basaran.data.remote.FirestoreDao
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
* Main view model holding the state values and performs state logic of the app.
 */
class MainViewModel(
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()
): ViewModel() {

    companion object {
        private const val KEY_SELECTED_BAUVORHABEN = "selectedBauvorhaben"
    }

    /////////////////////////// CONNECTIVITY //////////////////////////////////

    val isSyncedWithServer: StateFlow<Boolean> = FirestoreDao.isSyncedWithServer

    ///////////////////////////  FORM DATA   //////////////////////////////////

    // Create Bauvorhaben
    val bauvorhabenForm = BauvorhabenForm()

    /////////////////////////////////////////////////////////////

    private val _allBauvorhaben = MutableStateFlow<List<BauvorhabenDto>>(emptyList())
    val allBauvorhaben = _allBauvorhaben.asStateFlow()

    var selectedBauvorhaben = savedStateHandle
        .getStateFlow(KEY_SELECTED_BAUVORHABEN, null as BauvorhabenDto?)

    fun fetchAllBauvorhaben() {
        FirestoreDao.getAllBauvorhaben { task ->
            val docs = task.result.documents
            val dtoList = mutableListOf<BauvorhabenDto>()
            for (doc in docs) {
                dtoList.add(doc.toDto())
            }
            _allBauvorhaben.update { dtoList }
        }
    }

    fun createBauvorhaben(form: BauvorhabenForm) {
        FirestoreDao.createBauvorhaben(form.toDto()) { isSuccessful ->
            println("CreateBauvorhaben: isSuccessful=$isSuccessful")
        }
    }

    fun selectBauvorhaben(dto: BauvorhabenDto) {
        savedStateHandle[KEY_SELECTED_BAUVORHABEN] = dto
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
        .onEach { _isSearching.update { true } }
        .debounce(300L)
        .combine(_allBauvorhaben) { text, bauvorhabenList ->
            if(text.isBlank()) {
                bauvorhabenList
            } else {
                bauvorhabenList.filter { bauvorhaben ->
                    bauvorhaben.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _allBauvorhaben.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    private fun BauvorhabenDto.doesMatchSearchQuery(query: String): Boolean {
        return bauvorhaben.contains(query, ignoreCase = true)
    }

}