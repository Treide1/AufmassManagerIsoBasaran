package com.example.aufmassmanageriso_basaran.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


object FirestoreDao {

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().also {
           it.configure()
        }
    }

    private fun FirebaseFirestore.configure() {
        val settings = FirebaseFirestoreSettings.Builder(this.firestoreSettings)
            // Use memory-only cache
            .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
            // Use persistent disk cache (default)
            .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
            .build()
        this.firestoreSettings = settings
    }

    val _isSyncedWithServer = MutableStateFlow(false)
    val isSyncedWithServer = _isSyncedWithServer.asStateFlow()

    /////////////////////////////////////////////////////////////

    fun createBauvorhaben(dto: BauvorhabenDto, onComplete: (task: Task<DocumentReference>) -> Unit = {}) {
        val data = hashMapOf(
            "zeitstempel" to FieldValue.serverTimestamp(),
            "aufmassNummer" to dto.aufmassNummer,
            "auftragsNummer" to dto.auftragsNummer,
            "bauvorhaben" to dto.bauvorhaben,
            "notiz" to dto.notiz
        )

        db.collection("bauvorhaben").add(data)
            .addOnCompleteListener(onComplete)
            .addOnCompleteListener {
                _isSyncedWithServer.update { false }
                db.waitForPendingWrites()
                _isSyncedWithServer.update { true }
            }
    }

    fun getAllBauvorhaben(onComplete: (task: Task<QuerySnapshot>) -> Unit = {}) {
        db.collection("bauvorhaben").get()
            .addOnCompleteListener(onComplete)
            .addOnCompleteListener { task ->
                _isSyncedWithServer.update { task.result.metadata.isFromCache.not() }
            }
    }


}