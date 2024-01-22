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
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


object FirestoreRepo {

    /**
     * Tag for logging.
     */
    private const val TAG = "FirestoreRepo"

    /**
     * Firebase Firestore instance.
     */
    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().also {
           it.configure()
        }
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

        db.runTransaction{ transaction ->
            // Create document with attribute `name:  dto.bauvorhaben`
            transaction.set(bauvorhabenColl.document(), data)
            // Add this to the meta document via array union
            transaction.update(metaBauvorhabenDoc, "projection_name", FieldValue.arrayUnion(dto.name))
        }.addOnCompleteListener { task ->
            Log.d(TAG, "createBauvorhaben: Transaction complete. isSuccessful=${task.isSuccessful}")
            onResult(task.isSuccessful)
        }
    }

    fun getMetaBauvorhabenDoc(onSuccess: (doc: DocumentSnapshot) -> Unit, onFailure: (e: Exception) -> Unit) {
        Log.d(TAG, "getMetaBauvorhabenDoc: Fetching.")
        db.collection("meta").document("bauvorhaben").get()
            .addOnCompleteListener {
                Log.d(TAG, "getMetaBauvorhabenDoc: Fetched.")
            }
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
            .addOnCompleteListener (::updateIsSyncedWithServer)
    }

    fun getBauvorhabenByName(bauvorhabenName: String, onComplete: (task: Task<QuerySnapshot>) -> Unit = {}) {
        Log.d(TAG, "getBauvorhabenByName: Fetching. bauvorhabenName=$bauvorhabenName")
        db.collection("bauvorhaben").whereEqualTo("name", bauvorhabenName).get()
            .addOnCompleteListener {
                Log.d(TAG, "getBauvorhabenByName: Fetched.")
            }
            .addOnCompleteListener(onComplete)
            .addOnCompleteListener(::updateIsSyncedWithServer)
    }

    /////////////////////////////////////////////////////////////

    fun createEintragDoc(dto: EintragDto, bauvorhabenName: String, onResult: (isSuccess: Boolean) -> Unit) {
        Log.i(TAG, "createEintragDoc: Creating. dto=$dto, bauvorhabenName=$bauvorhabenName")

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
            "schmutzfilter" to dto.schmutzfilter,
            "dreiWegeVentil" to dto.dreiWegeVentil,
            "notiz" to dto.notiz,
            "zeitstempel" to FieldValue.serverTimestamp(),
        )

        // Get document with name = bauvorhabenName
        val bauvorhabenColl = db.collection("bauvorhaben")
        // Query for bauvorhabenDoc
        bauvorhabenColl.whereEqualTo("name", bauvorhabenName).get()
            // On failure, log error and return
            .addOnFailureListener {
                Log.e(TAG, "createEintragDoc: Could not fetch bauvorhabenDoc(s).", it)
                onResult(false)
            }
            // On success, check if exactly one bauvorhabenDoc was found
            // If not, log error and return.
            // If so, add eintragDoc to subcollection "eintraege" of bauvorhabenDoc.
            .addOnSuccessListener { snapshot ->
                Log.d(TAG, "createEintragDoc: Fetched bauvorhabenDoc(s): doc count=${snapshot.documents.size}")
                if (snapshot.documents.size != 1) {
                    Log.e(TAG, "createEintragDoc: Found n=${snapshot.documents.size} bauvorhaben with name $bauvorhabenName.")
                    onResult(false)
                    return@addOnSuccessListener
                }
                val bauvorhabenDoc = snapshot.documents.first()
                // Get subcollection "eintraege" of bauvorhabenDoc
                val eintraegeColl = bauvorhabenDoc.reference.collection("eintraege")
                // Create document of eintrag
                eintraegeColl.document().set(data)
                    .addOnCompleteListener { task ->
                        onResult(task.isSuccessful)
                    }
            }
    }

    /////////////////////////////////////////////////////////////

    fun createSpezialEintragDoc(dto: SpezialDto, bauvorhabenName: String, onResult: (isSuccess: Boolean) -> Unit) {
        Log.i(TAG, "createSpezialEintragDoc: Creating. dto=$dto, bauvorhabenName=$bauvorhabenName")

        val data = hashMapOf(
            "bereich" to dto.bereich,
            "daten" to dto.daten,
            "notiz" to dto.notiz,
            "zeitstempel" to FieldValue.serverTimestamp(),
        )

        // Get document with name = bauvorhabenName
        val bauvorhabenColl = db.collection("bauvorhaben")
        // Query for bauvorhabenDoc
        bauvorhabenColl.whereEqualTo("name", bauvorhabenName).get()
            // On failure, log error and return
            .addOnFailureListener {
                Log.e(TAG, "createSpezialEintragDoc: Could not fetch bauvorhabenDoc(s).", it)
                onResult(false)
            }
            // On success, check if exactly one bauvorhabenDoc was found
            // If not, log error and return.
            // If so, add eintragDoc to subcollection "spezialEintraege" of bauvorhabenDoc.
            .addOnSuccessListener { snapshot ->
                Log.d(TAG, "createSpezialEintragDoc: Fetched bauvorhabenDoc(s): doc count=${snapshot.documents.size}")
                if (snapshot.documents.size != 1) {
                    Log.e(TAG, "createSpezialEintragDoc: Found n=${snapshot.documents.size} bauvorhaben with name $bauvorhabenName.")
                    onResult(false)
                    return@addOnSuccessListener
                }
                val bauvorhabenDoc = snapshot.documents.first()
                // Get subcollection "spezialEintraege" of bauvorhabenDoc
                val spezialColl = bauvorhabenDoc.reference.collection("spezialEintraege")
                // Create document of eintrag
                spezialColl.document().set(data)
                    .addOnCompleteListener { task ->
                        onResult(task.isSuccessful)
                    }
            }
    }

    /////////////////////////////////////////////////////////////

    /**
     * Update the [isSyncedWithServer] state flow based on the result of the given [task].
     */
    private fun <T> updateIsSyncedWithServer(task: Task<T>) {
        Log.d(TAG, "updateIsSyncedWithServer: Invoked with task.isSuccessful=${task.isSuccessful}")
        try {
            when (val result = task.result) {
                is DocumentSnapshot -> _isSyncedWithServer.update { result.metadata.isFromCache.not() }
                is QuerySnapshot -> _isSyncedWithServer.update { result.metadata.isFromCache.not() }
                else -> throw Exception("Task result is neither DocumentSnapshot nor QuerySnapshot.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "updateIsSyncedWithServer: ", e)
            Log.w(TAG, "updateIsSyncedWithServer: Task failed. Assuming not synced with server.")
            _isSyncedWithServer.update { false }
        }
    }
}