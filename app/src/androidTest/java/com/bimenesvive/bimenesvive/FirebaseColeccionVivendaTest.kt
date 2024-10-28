package com.bimenesvive.bimenesvive

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseColeccionVivendaTest {

    @Test
    fun testViviendasCollectionIsNotEmpty() = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("viviendas")
        val documents = collection.get().await()
        val isNotEmpty = documents.isEmpty.not()
        Log.d("FirebaseTest", "testViviendasCollectionIsNotEmpty: $isNotEmpty")
        assertTrue(isNotEmpty)
    }
}