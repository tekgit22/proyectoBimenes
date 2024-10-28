package com.bimenesvive.bimenesvive

import org.junit.Test
import org.junit.Assert.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class, manifest = "src/main/AndroidManifest.xml", sdk = [28])
class UsuarioTest {

    private val firestore = FirebaseFirestore.getInstance()
    private val testUserId = "gB895udBWiaCKNMnGUidMbKyQii2"

    @Test
    fun subirCasaComprobarVivivenda() = runBlocking {
        val valoresAnuncio = mapOf(
            "titulo" to "Título Casa Prueba",
            "descripcion" to "Descripción Casa Prueba",
            "habitaciones" to 2,
            "metros" to 50,
            "ubicacion" to "San Julián",
            "telefono" to "666555444",
            "email" to "test@ejemplo.com",
            "userID" to testUserId,
            "localizacion" to GeoPoint(0.0, 0.0),
            "estado" to "VENTA",
            "precio" to 90000,
            "visitas" to 0,
            "disponible" to true
        )

        val referenciaAnuncio = firestore.collection("testing").document(testUserId).collection("anuncios").document("anuncioPrueba")
        referenciaAnuncio.set(valoresAnuncio).await()

        val snapshot = referenciaAnuncio.get().await()
        assertTrue(snapshot.exists())
        assertEquals("Título Casa Prueba", snapshot["titulo"])
        assertEquals(50, snapshot.getLong("metros")?.toInt())
        assertEquals(true, snapshot.getBoolean("disponible"))
        assertEquals("VENTA", snapshot["estado"])
    }

    @Test
    fun borrarAnuncioVivienda() = runBlocking {
        val referenciaAnuncio = firestore.collection("usuarios").document(testUserId).collection("anuncios").document("anuncioPrueba")
        referenciaAnuncio.delete().await()

        val snapshot = referenciaAnuncio.get().await()
        assertFalse(snapshot.exists())
    }
}