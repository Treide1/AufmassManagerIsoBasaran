package com.example.aufmassmanageriso_basaran.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aufmassmanageriso_basaran.data.remote.BauvorhabenDto
import com.example.aufmassmanageriso_basaran.data.remote.FirestoreDao
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.reflect.KProperty

/**
* Main view model holding the settings state and the entries state
 */
class MainViewModel: ViewModel() {

    private class StateFlowWrapper<T>(initial: T) {
        private val mutableStateFlow = MutableStateFlow(initial)
        val stateFlow = mutableStateFlow.asStateFlow()

        operator fun getValue(viewModel: ViewModel, property: KProperty<*>): T {
            return stateFlow.value!!
        }

        operator fun setValue(viewModel: ViewModel, property: KProperty<*>, value: T) {
            mutableStateFlow.update { value }
        }
    }

    /////////////////////////////////////////////////////////////

    val isSyncedWithServer: StateFlow<Boolean> = FirestoreDao.isSyncedWithServer

    /////////////////////////////////////////////////////////////

    //var allBauvorhaben by StateFlowWrapper<List<BauvorhabenDto>>(emptyList())
    private val _allBauvorhaben = MutableStateFlow<List<BauvorhabenDto>>(emptyList())
    val allBauvorhaben = _allBauvorhaben.asStateFlow()

    private val _selectedBauvorhaben = MutableStateFlow<BauvorhabenDto?>(null)
    var selectedBauvorhaben = _selectedBauvorhaben.asStateFlow()

    fun fetchBauvorhaben() {
        FirestoreDao.getAllBauvorhaben { task ->
            val docs = task.result?.documents
            val dtoList = mutableListOf<BauvorhabenDto>()
            for (doc in docs!!) {
                val dto = BauvorhabenDto(
                    bauvorhaben = doc["bauvorhaben"] as String,
                    aufmassNummer = doc["aufmassNummer"] as Long,
                    auftragsNummer = doc["auftragsNummer"] as Long?,
                    notiz = doc["notiz"] as String?
                )
                dtoList.add(dto)
            }
            _allBauvorhaben.update { dtoList }
        }
    }

    fun createBauvorhaben(dto: BauvorhabenDto) {
        FirestoreDao.createBauvorhaben(dto) { isSuccessful ->
            println("CreateBauvorhaben: $isSuccessful")
        }
    }

    fun selectBauvorhaben(dto: BauvorhabenDto) {
        _selectedBauvorhaben.update { dto }
    }


    /////////////////////////////////////////////////////////////

    // Search
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