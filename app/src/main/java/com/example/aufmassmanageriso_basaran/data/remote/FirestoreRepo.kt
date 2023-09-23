package com.example.aufmassmanageriso_basaran.data.remote

import android.util.Log
import com.example.aufmassmanageriso_basaran.data.remote.bauvorhaben.BauvorhabenDto
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
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

    fun createBauvorhaben(dto: BauvorhabenDto, onComplete: (task: Task<Transaction>) -> Unit = {}) {
        Log.i(TAG, "createBauvorhaben: dto=$dto, Creating...")

        val name = dto.name
        val data = hashMapOf(
            "name" to name,
            "aufmassNummer" to dto.aufmassNummer,
            "auftragsNummer" to dto.auftragsNummer,
            "notiz" to dto.notiz,
            "zeitstempel" to FieldValue.serverTimestamp(),
        )

        val bauvorhabenCol = db.collection("bauvorhaben")
        val metaBauvorhabenDoc = db.collection("meta").document("bauvorhaben")

        db.runTransaction { transaction ->
            // Create document with attribute `name:  dto.bauvorhaben`
            transaction.set(bauvorhabenCol.document(), data)
            // Add this to the meta document via array union
            transaction.update(metaBauvorhabenDoc, "projection_name", FieldValue.arrayUnion(dto.name))
        }.addOnCompleteListener(onComplete)
    }

    fun getMetaBauvorhabenDoc(onComplete: (task: Task<DocumentSnapshot>) -> Unit = {}) {
        Log.d(TAG, "getMetaBauvorhabenDoc: Fetching...")
        db.collection("meta").document("bauvorhaben").get()
            .addOnCompleteListener(onComplete)
            .addOnCompleteListener (::updateIsSyncedWithServer)
    }

    fun getBauvorhabenByName(bauvorhabenName: String, onComplete: (task: Task<QuerySnapshot>) -> Unit = {}) {
        Log.d(TAG, "getBauvorhabenByName: bauvorhabenName=$bauvorhabenName, Fetching...")
        db.collection("bauvorhaben").whereEqualTo("name", bauvorhabenName).get()
            .addOnCompleteListener(onComplete)
            .addOnCompleteListener(::updateIsSyncedWithServer)
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