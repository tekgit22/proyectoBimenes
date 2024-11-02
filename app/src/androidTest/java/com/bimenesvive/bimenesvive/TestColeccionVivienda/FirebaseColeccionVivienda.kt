package com.bimenesvive.bimenesvive.TestColeccionVivienda

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseColeccionVivienda {
    private val db = FirebaseFirestore.getInstance() // Instancia de Firestore

    /**
     * Función que comprueba si la colección de viviendas no está vacía
     * @return true si la colección no está vacía, false si está vacía
     */
    suspend fun isViviendasCollectionNotEmpty(): Boolean {
        val collection = db.collection("viviendas")
        val documentos = collection.get().await()
        return documentos.isEmpty.not()
    }
}