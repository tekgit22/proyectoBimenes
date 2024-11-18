package com.bimenesvive.bimenesvive.utilidades

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat



// METODOS GENERALES

/**
 * Solicita permisos en tiempo de ejecución a usuarios con SO Android 13 +
 * @param context Contexto de la actividad
 */
fun requestPhotoPermissions(context: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                1
            )
        }
    } else {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }
    }
}


// METODOS VIVIENDA

/**
 * Sube una foto de una vivienda a Firebase Storage y guarda la URL en Firestore Database
 * @param uri URI de la foto
 * @param userID ID del usuario
 * @param documentID ID del documento de la vivienda
 * @param storage Instancia de FirebaseStorage
 * @param context Contexto de la actividad
 * @param onSuccess Función que se ejecuta al subir la foto correctamente
 * @return URI de la foto subida
 */
fun subirFotoVivienda(
    uri: Uri,
    userID: String,
    documentID: String,
    storage: FirebaseStorage,
    context: Context,
    onSuccess: (Uri) -> Unit
) {
    val storageRef = storage.reference
    val fotoRef = storageRef.child("$userID/$documentID/${UUID.randomUUID()}.jpg")

    fotoRef.putFile(uri)
        .addOnSuccessListener {
            fotoRef.downloadUrl.addOnSuccessListener { nuevaURL ->
                guardarFotoVivienda(nuevaURL.toString(), userID, documentID)
                Toast.makeText(context, "Foto subida correctamente", Toast.LENGTH_SHORT).show()
                onSuccess(nuevaURL) // Este callback se ejecuta cuando se sube la foto correctamente
                // En PantallaVivienda, se añade la URL de la foto a la lista de fotos de la vivienda
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al subir la foto: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
}

/**
 * Guarda la URL de la foto en Firestore Database
 * @param url URL de la foto
 * @param userID ID del usuario
 * @param documentID ID del documento de la vivienda
 */
private fun guardarFotoVivienda(url: String, userID: String, documentID: String) {
    val db = FirebaseFirestore.getInstance()
    val urlFoto = hashMapOf(
        "url" to url
    )
    db.collection("viviendas").document(documentID).collection("fotos").add(urlFoto)
}

/**
 * Obtiene las URLs de las fotos de una vivienda desde Firestore
 * @param documentID ID del documento de la vivienda
 * @return List de URIs de las fotos de la vivienda
 */
suspend fun obtenerUrisFotosVivienda(documentID: String): List<Uri> {
    return try {
        val resultadoConsulta = FirebaseFirestore.getInstance()
            .collection("viviendas")
            .document(documentID)
            .collection("fotos")
            .get()
            .await()
        resultadoConsulta.documents.mapNotNull { it.getString("url")?.let { Uri.parse(it) } }
        // it refiere al documento actual de cada iteración
        // Uri.parse convierte la URL de la foto en un objeto Uri
    } catch (e: Exception) {
        Log.e("PantallaVivienda", "Error al obtener las fotos: ${e.message}")
        emptyList() // Fallback en caso de error. Esto asegura que la función devuelva una lista.
    }
}


//METODOS FINCA


/**
 * Sube una foto de una finca a Firebase Storage y guarda la URL en Firestore Database
 * @param uri URI de la foto
 * @param userID ID del usuario
 * @param documentID ID del documento de la finca
 * @param storage Instancia de FirebaseStorage
 * @param context Contexto de la actividad
 * @param onSuccess Función que se ejecuta al subir la foto correctamente
 * @return URI de la foto subida
 */
fun subirFotoFinca(
    uri: Uri,
    userID: String,
    documentID: String,
    storage: FirebaseStorage,
    context: Context,
    onSuccess: (Uri) -> Unit
) {
    val storageRef = storage.reference
    val fotoRef = storageRef.child("$userID/$documentID/${UUID.randomUUID()}.jpg")

    fotoRef.putFile(uri)
        .addOnSuccessListener {
            fotoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                guardarFotoFinca(downloadUri.toString(), userID, documentID)
                Toast.makeText(context, "Foto subida correctamente", Toast.LENGTH_SHORT).show()
                onSuccess(downloadUri)
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al subir la foto: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
}

/**
 * Guarda la URL de la foto en Firestore
 * @param url URL de la foto
 * @param userID ID del usuario
 * @param documentID ID del documento de la finca
 */
fun guardarFotoFinca(url: String, userID: String, documentID: String) {
    val db = FirebaseFirestore.getInstance()
    val urlFoto = hashMapOf(
        "url" to url
    )
    db.collection("fincas").document(documentID).collection("fotos").add(urlFoto)
}

/**
 * Obtiene las URLs de las fotos de una finca desde Firestore
 * @param documentID ID del documento de la finca
 * @return List de URIs de las fotos de la finca
 */
suspend fun obtenerUrisFotosFinca(documentID: String): List<Uri> {
    return try {
        val resultadoConsulta = FirebaseFirestore.getInstance()
            .collection("fincas")
            .document(documentID)
            .collection("fotos")
            .get()
            .await()
        resultadoConsulta.documents.mapNotNull { it.getString("url")?.let { Uri.parse(it) } }

    } catch (e: Exception) {
        Log.e("PantallaFinca", "Error al obtener las fotos: ${e.message}")
        emptyList()
    }
}





