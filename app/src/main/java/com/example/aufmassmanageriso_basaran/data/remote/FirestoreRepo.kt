package com.example.aufmassmanageriso_basaran.data.remote

import android.util.Log
import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.BauvorhabenDto
import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.EintragDto
import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.SpezialDto
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking


object FirestoreRepo {

    /**
     * Tag for logging.
     */
    private const val TAG = "FirestoreRepo"

    /**
     * Delay for offline write to give the illusion work being done
     */
    private const val OFFLINE_WRITE_DELAY_MILLIS = 300L

    /**
     * Firebase Firestore instance.
     */
    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply { configure() }
    }

    /**
     * Configure the Firestore instance. Required only once after app start.
     */
    private fun FirebaseFirestore.configure() {
        val settings = FirebaseFirestoreSettings.Builder(this.firestoreSettings)
            // Use memory-only cache
            .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
            // Use persistent disk cache (default, but explicitly set)
            .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
            .build()
        this.firestoreSettings = settings
    }

    // Mutable backing field of isSyncedWithServer.
    private val _isSyncedWithServer = MutableStateFlow(false)

    /**
     * Public state flow indicating whether the Firestore instance is synced with the server.
     * If it isn't in sync, the actions are stored locally and will be synced with the server
     * when the connection is reestablished.
     */
    val isSyncedWithServer = _isSyncedWithServer.asStateFlow()

    private var listener: ListenerRegistration? = null

    fun startConnectionListener() {
        Log.d(TAG, "startConnectionListener: Starting.")
        listener = db.collection("meta").document("fetch_me")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "checkSyncedWithServer: Listen failed.", e)
                    return@addSnapshotListener
                }
                // If snapshot is not null and isFromCache is true, then the data is local.
                val isFromCache = snapshot?.metadata?.isFromCache ?: false
                val source = if (isFromCache) "Local" else "Server"

                Log.d(TAG, "checkSyncedWithServer: source=$source")
                _isSyncedWithServer.update { isFromCache.not() }
            }
    }

    fun stopConnectionListener() {
        Log.d(TAG, "stopConnectionListener: Stopping.")
        listener?.remove()
    }

    private fun illusionOfWorkDelay() {
        runBlocking {
            delay(OFFLINE_WRITE_DELAY_MILLIS)
        }
    }

    /////////////////////////////////////////////////////////////

    fun createBauvorhabenDoc(dto: BauvorhabenDto, onResult: (isSuccess: Boolean) -> Unit) {
        Log.i(TAG, "createBauvorhaben: Creating. dto=$dto")

        val data = hashMapOf(
            "name" to dto.name,
            "aufmassNummer" to dto.aufmassNummer,
            "auftragsNummer" to dto.auftragsNummer,
            "notiz" to dto.notiz,
            "zeitstempel" to FieldValue.serverTimestamp(),
        )

        val bauvorhabenColl = db.collection("bauvorhaben")
        val metaBauvorhabenDoc = db.collection("meta").document("bauvorhaben")

        db.runTransaction { transaction ->
            // Create document with attribute `name:  dto.bauvorhaben`
            transaction.set(bauvorhabenColl.document(), data)
            // Add this to the meta document via array union
            transaction.update(metaBauvorhabenDoc, "projection_name", FieldValue.arrayUnion(dto.name))
        }.addOnCompleteListener { task ->
            Log.d(TAG, "createBauvorhaben: Transaction complete. isSuccessful=${task.isSuccessful}")
        }

        // Give the illusion of work being done. Then call onResult.
        illusionOfWorkDelay()
        onResult(true)
    }

    fun getMetaBauvorhabenDoc(onSuccess: (doc: DocumentSnapshot) -> Unit, onFailure: (e: Exception) -> Unit) {
        Log.d(TAG, "getMetaBauvorhabenDoc: Fetching.")
        db.collection("meta").document("bauvorhaben").get()
            .addOnCompleteListener {
                Log.d(TAG, "getMetaBauvorhabenDoc: Fetched.")
            }
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    fun getBauvorhabenByName(bauvorhabenName: String, onComplete: (task: Task<QuerySnapshot>) -> Unit = {}) {
        Log.d(TAG, "getBauvorhabenByName: Fetching by bauvorhabenName='$bauvorhabenName'.")
        db.collection("bauvorhaben").whereEqualTo("name", bauvorhabenName).get()
            .addOnCompleteListener {
                Log.d(TAG, "getBauvorhabenByName: Fetched.")
            }
            .addOnCompleteListener(onComplete)
    }

    /////////////////////////////////////////////////////////////

    fun createEintragDoc(dto: EintragDto, docId: String, onResult: (isSuccess: Boolean) -> Unit) {
        Log.i(TAG, "createEintragDoc: Creating. dto=$dto, docId=$docId")

        val data = hashMapOf(
            "bereich" to dto.bereich,
            "durchmesser" to dto.durchmesser,
            "isolierung" to dto.isolierung,
            "gewerk" to dto.gewerk,
            "meterListe" to dto.meterListe,
            "meterSumme" to dto.meterSumme,
            "bogen" to dto.bogen,
            "stutzen" to dto.stutzen,
            "ausschnitt" to dto.ausschnitt,
            "passstueck" to dto.passstueck,
            "endstelle" to dto.endstelle,
            "halter" to dto.halter,
            "flansch" to dto.flansch,
            "ventil" to dto.ventil,
            "schmutzfaenger" to dto.schmutzfaenger,
            "dreiWegeVentil" to dto.dreiWegeVentil,
            "notiz" to dto.notiz,
            "zeitstempel" to FieldValue.serverTimestamp(),
        )

        // Get document with name = bauvorhabenName
        val bauvorhabenDoc = db.collection("bauvorhaben").document(docId)
        // Get subcollection "eintraege" of bauvorhabenDoc
        val eintraegeColl = bauvorhabenDoc.collection("eintraege")
        // Create document of eintrag
        eintraegeColl.document().set(data).addOnCompleteListener {
            Log.d(TAG, "createEintragDoc: Created. isSuccess=${it.isSuccessful}")
        }

        // Give the illusion of work being done. Then call onResult.
        illusionOfWorkDelay()
        onResult(true)
    }

    /////////////////////////////////////////////////////////////

    fun createSpezialEintragDoc(dto: SpezialDto, docId: String, onResult: (isSuccess: Boolean) -> Unit) {
        Log.i(TAG, "createSpezialEintragDoc: Creating. dto=$dto, docId=$docId")

        val data = hashMapOf(
            "bereich" to dto.bereich,
            "daten" to dto.daten,
            "notiz" to dto.notiz,
            "zeitstempel" to FieldValue.serverTimestamp(),
        )

        // Get document with name = bauvorhabenName
        val bauvorhabenDoc = db.collection("bauvorhaben").document(docId)
        // Get subcollection "spezialEintraege" of bauvorhabenDoc
        val spezialColl = bauvorhabenDoc.collection("spezialEintraege")
        // Create document of eintrag
        spezialColl.document().set(data).addOnCompleteListener {
            Log.d(TAG, "createSpezialEintragDoc: Created. isSuccess=${it.isSuccessful}")
        }

        // Give the illusion of work being done. Then call onResult.
        illusionOfWorkDelay()
        onResult(true)
    }
}