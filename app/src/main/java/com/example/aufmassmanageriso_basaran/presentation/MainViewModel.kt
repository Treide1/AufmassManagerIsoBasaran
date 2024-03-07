package com.example.aufmassmanageriso_basaran.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aufmassmanageriso_basaran.data.excel.BAUVORHABEN_EXCEL_HEADER_ROW
import com.example.aufmassmanageriso_basaran.data.excel.createSheetWithPivotTable
import com.example.aufmassmanageriso_basaran.data.excel.toExcelContentRow
import com.example.aufmassmanageriso_basaran.data.local.BauvorhabenForm
import com.example.aufmassmanageriso_basaran.data.local.EintragForm
import com.example.aufmassmanageriso_basaran.data.local.SpezialForm
import com.example.aufmassmanageriso_basaran.data.mapping.toDto
import com.example.aufmassmanageriso_basaran.data.remote.FirestoreRepo
import com.example.aufmassmanageriso_basaran.data.settings.SettingsRepo
import com.example.aufmassmanageriso_basaran.data.zip.FileRepo
import com.example.aufmassmanageriso_basaran.data.utility.logging.Logger
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
import kotlinx.coroutines.runBlocking
import org.apache.poi.hssf.usermodel.HSSFWorkbook

/**
* Main view model holding the state values and performs state logic of the app.
 */
@Suppress("MemberVisibilityCanBePrivate")
class MainViewModel(
    private val settingsRepo: SettingsRepo,
    private val displayMsgToUser: (msg: String) -> Unit,
): ViewModel() {

    companion object {
        private val logger = Logger("MainViewModel")
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
        logger.d("createBauvorhaben: Creating.")
        if (form.validate().not()) {
            logger.d("createBauvorhaben: Validation failed.")
            displayMsgToUser("Bitte fülle alle Pflichtfelder aus.")
            return
        }
        logger.d("createBauvorhaben: Validation success. form=$form")

        FirestoreRepo.createBauvorhabenDoc(form.toDto()) { isSuccess ->
            logger.d("createBauvorhaben: createBauvorhabenDoc with isSuccess=$isSuccess")
            if (isSuccess.not()) {
                logger.e("createBauvorhaben: Could not create bauvorhaben.")
                displayMsgToUser("Fehler beim Erstellen des Bauvorhabens.")
                return@createBauvorhabenDoc
            }
            logger.d("createBauvorhaben: Created.")
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
            logger.d("selectBauvorhaben: selectedBauvorhaben=$selectedBauvorhaben")
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    @Suppress("UNCHECKED_CAST") // Justification: We know the type of the document.
    fun fetchBauvorhabenNames() {
        logger.d("fetchBauvorhabenNames: Fetching...")
        FirestoreRepo.getMetaBauvorhabenDoc(
            onSuccess = { doc ->
                logger.d("fetchBauvorhabenNames: Fetched.")
                val projection = doc.get("projection_name") as List<String>
                _bauvorhabenNames.update { projection }
            },
            onFailure = { e ->
                logger.e("fetchBauvorhabenNames: getMetaBauvorhabenDoc with failure.", e)
                displayMsgToUser("Fehler: "+ (e.message ?: "(Keine Fehler-Nachricht erhalten.)"))
            }
        )
    }

    /**
     * Selects bauvorhaben by [bauvorhabenName]. If null, deselects any bauvorhaben.
     */
    fun selectBauvorhaben(bauvorhabenName: String?) {
        logger.d("selectBauvorhaben: Selecting for bauvorhabenName=$bauvorhabenName.")
        if (bauvorhabenName == null) {
            viewModelScope.launch {
                logger.d("selectBauvorhaben: bauvorhabenName is null. Thus deselecting.")
                settingsRepo.removeSelectedBauvorhaben()
            }
            return
        }
        if (isSyncedWithServer.value.not()) {
            logger.e("selectBauvorhaben: Not synced with server.")
            displayMsgToUser("Fehler: Auswahl kann nur mit Internetverbindung erfolgen.")
            return
        }

        // Fetch bauvorhabenDto
        FirestoreRepo.getBauvorhabenByName(bauvorhabenName) { task ->
            viewModelScope.launch {
                logger.d("selectBauvorhaben: getBauvorhabenByName resulted in isSuccessful=${task.isSuccessful}")
                val docs = task.result.documents
                // If 0 or 2+ bauvorhaben with same name exist, log error and return.
                if (docs.size != 1) {
                    logger.e("selectBauvorhaben: Found n=${docs.size} bauvorhaben docs with name '$bauvorhabenName'.")
                    displayMsgToUser("Fehler beim Auswählen des Bauvorhabens.")
                    return@launch
                }

                // Update selected bauvorhaben in user preferences
                logger.d("selectBauvorhaben: Updating selected bauvorhaben for SettingsRepo.")
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

    fun createEintrag() {
        val docId = selectedBauvorhaben.value?._docID
        logger.d("createEintrag: Creating. docId=$docId")

        if (eintragForm.validate().not()) {
            logger.d("createEintrag: Validation failed.")
            displayMsgToUser("Bitte fülle alle Pflichtfelder aus.")
            return
        }
        logger.d("createEintrag: Validation success.")

        if (docId == null) {
            logger.e("createEintrag: docId is null.")
            displayMsgToUser("Fehler beim Erstellen des Eintrags.")
            return
        }

        FirestoreRepo.createEintragDoc(eintragForm.toDto(), docId) { isSuccess ->
            logger.d("createEintrag: createEintragDoc with isSuccess=$isSuccess")
            if (isSuccess.not()) {
                logger.e("createEintrag: Could not create eintrag.")
                displayMsgToUser("Fehler beim Erstellen des Eintrags.")
                return@createEintragDoc
            }
            logger.d("createEintrag: Created.")
            displayMsgToUser("Eintrag wurde erstellt.")

            eintragForm.clearFields()
        }
    }

    /////////////////////////////////////////////////////////////

    // Create Spezial (a.k.a. SpezialEintrag)
    val spezialForm = SpezialForm()

    fun createSpezial() {
        val docId = selectedBauvorhaben.value?._docID
        logger.d("createSpezial: Creating. form=$spezialForm, docId=$docId")

        if (spezialForm.validate().not()) {
            logger.d("createSpezial: Validation failed.")
            displayMsgToUser("Bitte fülle alle Pflichtfelder aus.")
            return
        }
        logger.d("createSpezial: Validation success.")

        if (docId == null) {
            logger.e("createSpezial: docId is null.")
            displayMsgToUser("Fehler beim Erstellen des Spezial-Eintrags.")
            return
        }

        FirestoreRepo.createSpezialEintragDoc(spezialForm.toDto(), docId) { isSuccess ->
            logger.d("createSpezial: createSpezialEintragDoc with isSuccess=$isSuccess")
            if (isSuccess.not()) {
                logger.e("createSpezial: Could not create spezialEintrag.")
                displayMsgToUser("Fehler beim Erstellen des Spezial-Eintrags.")
                return@createSpezialEintragDoc
            }
            logger.d("createSpezial: Created. ")
            displayMsgToUser("Spezial-Eintrag wurde erstellt.")

            spezialForm.clearFields()
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

    ///////////////////////////   FILES   //////////////////////////////////

    fun downloadBackup() {
        logger.d("downloadBackup: Opening Location Picker and download on result.")
        FileRepo.launchBackupDownloadPicker()
    }

    // TODO: Use `selectedBauvorhaben` to fetch data and put into excel workbook
    fun exportBauvorhaben() {
        val docId = selectedBauvorhaben.value?._docID
        val name = selectedBauvorhaben.value?.name
        logger.d("exportBauvorhaben: Exporting for name='$name' and docID=$docId")
        if (docId == null) {
            logger.e("exportBauvorhaben: docID is null.")
            // TODO: Fetch docId if missing for some reason, instead of hard return
            displayMsgToUser("Syncronisierung erforderlich. Bitte gehe zu 'Bauvorhaben auswählen'.")
            return
        }

        // Setting up exporting process
        FileRepo.createWorkbookForExport = {
            logger.d("exportBauvorhaben: Creating workbook.")
            val excelExportData = runBlocking {
                FirestoreRepo.getExcelExportData(docId)
            }
            logger.i("exportBauvorhaben: excelExportData=$excelExportData")

            val workbook = HSSFWorkbook().apply workbook@{
                if (excelExportData == null) {
                    logger.e("exportBauvorhaben: excelExportData is null.")
                    displayMsgToUser("Fehler beim Exportieren.")
                    return@workbook
                }

                val sheetContent = listOf(
                    BAUVORHABEN_EXCEL_HEADER_ROW,
                    excelExportData.toExcelContentRow()
                )
                createSheetWithPivotTable("Bauvorhaben", sheetContent)
            }
            logger.d("exportBauvorhaben: Created workbook.")
            workbook
        }
        FileRepo.onWorkbookExported = {
            logger.d("exportBauvorhaben: Workbook exported.")
            displayMsgToUser("Bauvorhaben wurde exportiert.")
            FirestoreRepo.markExcelDataAsExported(docId)
        }
        // Launching export picker
        displayMsgToUser("Download-Ort erforderlich")
        logger.d("exportBauvorhaben: Launching export picker.")
        FileRepo.launchExportExcelPicker("test")
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