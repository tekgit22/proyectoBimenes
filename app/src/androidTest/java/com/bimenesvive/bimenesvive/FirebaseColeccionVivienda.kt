package com.bimenesvive.bimenesvive

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseColeccionVivienda {
    private val db = FirebaseFirestore.getInstance()

    suspend fun isViviendasCollectionNotEmpty(): Boolean {
        val collection = db.collection("viviendas")
        val documents = collection.get().await()
        return documents.isEmpty.not()
    }
}