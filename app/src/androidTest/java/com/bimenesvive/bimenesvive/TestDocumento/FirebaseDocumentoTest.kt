package com.bimenesvive.bimenesvive.TestDocumento

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

// Este test lo hice con una sola clase, sin necesidad de una auxiliar
// La pega que tiene es que en cada test obtengo una nueva instancia de Firestore, lo que puede ser ineficiente
// Comparado con el test de la colección de viviendas, que tiene una clase auxiliar que guarda la instancia de Firestore

@RunWith(AndroidJUnit4::class) // Necesario para poder correr tests en Android, porque se usa runBlocking
class FirebaseTestCollectionTest {

    /**
     * Comprueba que un documento de la colección "test" existe
     */
    @Test
    fun testExisteDocumento() = runBlocking {  // runBlocking es necesario para poder usar await()
        val firestore = FirebaseFirestore.getInstance()
        // Referencia al documento
        val documento = firestore.collection("test").document("Bsijt3Qp0aHB9bfiES5X")
        // Obtiene el contenido del documento
        val instantanea =
            documento.get().await() // await() es necesario para esperar a que se resuelva la tarea
        assertTrue("El documento debería existir", instantanea.exists())
    }

    /**
     * Comprueba que el documento contiene la clave "clave"
     */
    @Test
    fun testDocumentoTieneClave() = runBlocking {
        val firestore = FirebaseFirestore.getInstance()
        val documento = firestore.collection("test").document("Bsijt3Qp0aHB9bfiES5X")
        val instantanea = documento.get().await()
        val valor = instantanea.getString("clave")
        assertNotNull("El documento debería tener la clave 'clave'", valor)
    }

    /**
     * Comprueba que el documento contiene la clave "clave" tiene como valor "valor de prueba"
     */
    @Test
    fun testDocumentoValorCorrecto() = runBlocking {
        val firestore = FirebaseFirestore.getInstance()
        val documento = firestore.collection("test").document("Bsijt3Qp0aHB9bfiES5X")
        val instantanea = documento.get().await()
        val valor = instantanea.getString("clave")
        assertEquals("El valor de la clave debería ser 'valor de prueba'", "valor de prueba", valor)
    }


    /**
     * Comprueba que el documento "NoExiste" de la colección "test" no existe
     */
    @Test
    fun testDocumentoInexistente() = runBlocking {
        val firestore = FirebaseFirestore.getInstance()
        val documento = firestore.collection("test").document("NoExiste")
        val instantanea = documento.get().await()
        assertFalse("El documento no debería existir", instantanea.exists())
    }


    /**
     * Comprueba que el documento no contiene la clave "ClaveInexistente"
     */
    @Test
    fun testDocumentoClaveInexistente() = runBlocking {
        val firestore = FirebaseFirestore.getInstance()
        val documento = firestore.collection("test").document("Bsijt3Qp0aHB9bfiES5X")
        val instantanea = documento.get().await()
        val valor = instantanea.getString("ClaveInexistente")
        assertNull("El documento no debería tener la clave 'ClaveInexistente'", valor)
    }


    /**
     * Comprueba que la clave "clave" tiene un valor distinto a "valor incorrecto"
     */
    @Test
    fun testDocumentoValorIncorrecto() = runBlocking {
        val firestore = FirebaseFirestore.getInstance()
        val documento = firestore.collection("test").document("Bsijt3Qp0aHB9bfiES5X")
        val instantanea = documento.get().await()
        val valor = instantanea.getString("clave")
        assertNotEquals(
            "El valor de la clave no debería ser 'valor incorrecto'",
            "valor incorrecto",
            valor
        )
    }
}