package com.example.aufmassmanageriso_basaran.data.remote

import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.BauvorhabenDto
import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.EintragDto
import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.SpezialDto
import com.example.aufmassmanageriso_basaran.logging.Logger
import com.example.aufmassmanageriso_basaran.logging.replaceZeitstempel
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

    private val logger = Logger("FirestoreRepo")

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
        logger.d("startConnectionListener: Starting.")
        listener = db.collection("meta").document("fetch_me")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
                if (e != null) {
                    logger.w("startConnectionListener: Listen failed.", e)
                    return@addSnapshotListener
                }
                // If snapshot is not null and isFromCache is true, then the data is local.
                val isFromCache = snapshot?.metadata?.isFromCache ?: false
                val source = if (isFromCache) "Local" else "Server"

                logger.d("startConnectionListener: source=$source")
                _isSyncedWithServer.update { isFromCache.not() }
            }
    }

    fun stopConnectionListener() {
        logger.d("stopConnectionListener: Stopping.")
        listener?.remove()
    }

    private fun illusionOfWorkDelay() {
        runBlocking {
            delay(OFFLINE_WRITE_DELAY_MILLIS)
        }
    }

    /////////////////////////////////////////////////////////////

    fun createBauvorhabenDoc(dto: BauvorhabenDto, onResult: (isSuccess: Boolean) -> Unit) {
        logger.i("createBauvorhaben: Creating. dto=$dto")

        val data = hashMapOf(
            "name" to dto.name,
            "aufmassNummer" to dto.aufmassNummer,
            "auftragsNummer" to dto.auftragsNummer,
            "notiz" to dto.notiz,
            "zeitstempel" to FieldValue.serverTimestamp(),
        )
        logger.logObject(listOf("createBauvorhaben"), data.replaceZeitstempel())

        val bauvorhabenColl = db.collection("bauvorhaben")
        val metaBauvorhabenDoc = db.collection("meta").document("bauvorhaben")

        db.runTransaction { transaction ->
            // Create document with attribute `name:  dto.bauvorhaben`
            transaction.set(bauvorhabenColl.document(), data)
            // Add this to the meta document via array union
            transaction.update(metaBauvorhabenDoc, "projection_name", FieldValue.arrayUnion(dto.name))
        }.addOnCompleteListener { task ->
            logger.d("createBauvorhaben: Transaction complete. isSuccessful=${task.isSuccessful}")
        }

        // Give the illusion of work being done. Then call onResult.
        illusionOfWorkDelay()
        onResult(true)
    }

    fun getMetaBauvorhabenDoc(onSuccess: (doc: DocumentSnapshot) -> Unit, onFailure: (e: Exception) -> Unit) {
        logger.d("getMetaBauvorhabenDoc: Fetching.")
        db.collection("meta").document("bauvorhaben").get()
            .addOnCompleteListener {
                logger.d("getMetaBauvorhabenDoc: Fetched.")
            }
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    fun getBauvorhabenByName(bauvorhabenName: String, onComplete: (task: Task<QuerySnapshot>) -> Unit = {}) {
        logger.d("getBauvorhabenByName: Fetching by bauvorhabenName='$bauvorhabenName'.")
        db.collection("bauvorhaben").whereEqualTo("name", bauvorhabenName).get()
            .addOnCompleteListener {
                logger.d("getBauvorhabenByName: Fetched.")
            }
            .addOnCompleteListener(onComplete)
    }

    /////////////////////////////////////////////////////////////

    fun createEintragDoc(dto: EintragDto, docId: String, onResult: (isSuccess: Boolean) -> Unit) {
        logger.i("createEintragDoc: Creating. dto=$dto, docId=$docId")

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
        logger.logObject(listOf("createEintragDoc"), data.replaceZeitstempel())

        // Get document with name = bauvorhabenName
        val bauvorhabenDoc = db.collection("bauvorhaben").document(docId)
        // Get subcollection "eintraege" of bauvorhabenDoc
        val eintraegeColl = bauvorhabenDoc.collection("eintraege")
        // Create document of eintrag
        eintraegeColl.document().set(data).addOnCompleteListener {
            logger.d("createEintragDoc: Created. isSuccess=${it.isSuccessful}")
        }

        // Give the illusion of work being done. Then call onResult.
        illusionOfWorkDelay()
        onResult(true)
    }

    /////////////////////////////////////////////////////////////

    fun createSpezialEintragDoc(dto: SpezialDto, docId: String, onResult: (isSuccess: Boolean) -> Unit) {
        logger.i("createSpezialEintragDoc: Creating. dto=$dto, docId=$docId")

        val data = hashMapOf(
            "bereich" to dto.bereich,
            "daten" to dto.daten,
            "notiz" to dto.notiz,
            "zeitstempel" to FieldValue.serverTimestamp(),
        )
        logger.logObject(listOf("createSpezialEintragDoc"), data.replaceZeitstempel())

        // Get document with name = bauvorhabenName
        val bauvorhabenDoc = db.collection("bauvorhaben").document(docId)
        // Get subcollection "spezialEintraege" of bauvorhabenDoc
        val spezialColl = bauvorhabenDoc.collection("spezialEintraege")
        // Create document of eintrag
        spezialColl.document().set(data).addOnCompleteListener {
            logger.d("createSpezialEintragDoc: Created. isSuccess=${it.isSuccessful}")
        }

        // Give the illusion of work being done. Then call onResult.
        illusionOfWorkDelay()
        onResult(true)
    }
}