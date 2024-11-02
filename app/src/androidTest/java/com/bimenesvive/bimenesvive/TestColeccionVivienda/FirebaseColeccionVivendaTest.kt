package com.bimenesvive.bimenesvive.TestColeccionVivienda

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) // Necesario para poder correr tests en Android
class FirebaseColeccionVivendaTest {

    /**
     * Test que comprueba que la colección de viviendas no está vacía
     * @return true si la colección no está vacía, false si está vacía
     */
    @Test
    fun testViviendasCollectionIsNotEmpty() = runBlocking {
        val firebase = FirebaseFirestore.getInstance() // Instancia de Firestore
        val collection = firebase.collection("viviendas") // Colección de viviendas
        val documentos = collection.get().await() // Obtengo los ocumentos de la colección (espera hasta que se resuelva)
        val isNotEmpty = documentos.isEmpty.not() // Compruebo si la colección no está vacía
        Log.d("FirebaseTest", "testViviendasCollectionIsNotEmpty: $isNotEmpty") // Log para ver el resultado
        assertTrue(isNotEmpty) // Devuelvo el resultado
    }
}