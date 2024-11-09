package com.bimenesvive.bimenesvive.utilidades

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.bimenesvive.bimenesvive.R
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
import coil.transform.CircleCropTransformation


/**
 * Solicita permisos en tiempo de ejecución a usuarios con SO Android 13 +
  */
fun requestPhotoPermissions(context: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1)
        }
    } else {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }
}



// METODOS VIVIENDA

/**
 * Sube una foto de una vivienda a Firebase Storage y guarda la URL en Firestore
 */
fun subirFotoVivienda(uri: Uri, userID: String, documentID: String, storage: FirebaseStorage, context: Context, onSuccess: (Uri) -> Unit) {
    val storageRef = storage.reference
    val photoRef = storageRef.child("$userID/$documentID/${UUID.randomUUID()}.jpg")

    photoRef.putFile(uri)
        .addOnSuccessListener {
            photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                guardarFotoVivienda(downloadUri.toString(), userID, documentID)
                Toast.makeText(context, "Foto subida correctamente", Toast.LENGTH_SHORT).show()
                onSuccess(downloadUri)
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al subir la foto: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

/**
 * Guarda la URL de la foto en Firestore
 */
private fun guardarFotoVivienda(url: String, userID: String, documentID: String) {
    val db = FirebaseFirestore.getInstance()
    val photoData = hashMapOf(
        "url" to url
    )
    db.collection("viviendas").document(documentID).collection("fotos").add(photoData)
}

/**
 * Obtiene las URLs de las fotos de una vivienda desde Firestore
 */
suspend fun getFotosViviendaDesdeFirestore(documentID: String): List<Uri> {
    return try {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("viviendas")
            .document(documentID)
            .collection("fotos")
            .get()
            .await()
        snapshot.documents.mapNotNull { it.getString("url")?.let { Uri.parse(it) } }
    } catch (e: Exception) {
        Log.e("PantallaVivienda", "Error al obtener las fotos: ${e.message}")
        emptyList()
    }
}




//METODOS FINCA


/**
 * Sube una foto de una finca a Firebase Storage y guarda la URL en Firestore
 */
fun subirFotoFinca(uri: Uri, userID: String, documentID: String, storage: FirebaseStorage, context: Context, onSuccess: (Uri) -> Unit) {
    val storageRef = storage.reference
    val photoRef = storageRef.child("$userID/$documentID/${UUID.randomUUID()}.jpg")

    photoRef.putFile(uri)
        .addOnSuccessListener {
            photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                guardarFotoFinca(downloadUri.toString(), userID, documentID)
                Toast.makeText(context, "Foto subida correctamente", Toast.LENGTH_SHORT).show()
                onSuccess(downloadUri)
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al subir la foto: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

/**
 * Guarda la URL de la foto en Firestore
 */
fun guardarFotoFinca(url: String, userID: String, documentID: String) {
    val db = FirebaseFirestore.getInstance()
    val photoData = hashMapOf(
        "url" to url
    )
    db.collection("fincas").document(documentID).collection("fotos").add(photoData)
}

/**
 * Obtiene las URLs de las fotos de una finca desde Firestore
 */
suspend fun getFotosFincaDesdeFirestore(documentID: String): List<Uri> {
    return try {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("fincas")
            .document(documentID)
            .collection("fotos")
            .get()
            .await()
        snapshot.documents.mapNotNull { it.getString("url")?.let { Uri.parse(it) } }
    } catch (e: Exception) {
        Log.e("PantallaFinca", "Error al obtener las fotos: ${e.message}")
        emptyList()
    }
}


/**
 * Muestra una imagen con un placeholder mientras se carga
 */
@Composable
fun ImagenConPlaceholder(uri: Uri, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(uri)
            .size(Size.ORIGINAL)
            .placeholder(R.drawable.loading_spinner)
            .crossfade(true)
            .transformations(CircleCropTransformation())
            .build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
    )
}


