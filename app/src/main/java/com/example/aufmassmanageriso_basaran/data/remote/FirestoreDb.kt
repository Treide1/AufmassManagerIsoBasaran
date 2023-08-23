package com.example.aufmassmanageriso_basaran.data.remote


import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object FirestoreDb {

    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun getAllgemein(): String = suspendCoroutine { cont ->
        db.collection("allgemein").get().addOnCompleteListener { task ->
            val docs = task.result?.documents
            var result = "Result: \n"
            for (doc in docs!!) {
                result += doc.id + " => " + doc.data.toString() + "\n"

                doc.get("createTime")
            }
            cont.resume(result)
        }
    }

    suspend fun createAllgemein(bauvorhaben: String, allgemeineNotiz: String = ""): String = suspendCoroutine { cont ->
        val data = hashMapOf(
            "zeitstempel" to FieldValue.serverTimestamp(),
            "aufmassNummer" to 1,
            "auftragsNummer" to -1,
            "bauvorhaben" to bauvorhaben,
            "allgemeineNotiz" to allgemeineNotiz
        )

        db.collection("allgemein").add(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                cont.resume("DocumentSnapshot added with ID: " + task.result?.id)
            } else {
                cont.resume("Error adding document")
            }
        }
    }
}