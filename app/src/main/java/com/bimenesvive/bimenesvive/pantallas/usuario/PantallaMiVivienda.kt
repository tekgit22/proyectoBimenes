package com.bimenesvive.bimenesvive.pantallas.usuario

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.bimenesvive.bimenesvive.modelo.Vivienda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

@Composable
fun PantallaMiVivienda(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    documentID: String
) {
    var vivienda by remember { mutableStateOf<Vivienda?>(null) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current as Activity
    val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return

    // Solicitar permisos en tiempo de ejecución a usuarios con SO Android 13 +
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1)
        }
    } else {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

    LaunchedEffect(documentID) {
        vivienda = getMiViviendaById(documentID)
        imageUris = getFotosFromFirestore(documentID)
        Log.d("PantallaVivienda", "Vivienda: $vivienda")
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.size <= 5) {
            uris.forEach { uri ->
                subirFoto(uri, userID, documentID, FirebaseStorage.getInstance(), context) { downloadUri ->
                    imageUris = imageUris + downloadUri
                }
            }
        } else {
            Toast.makeText(context, "Puedes subir hasta 5 fotos", Toast.LENGTH_SHORT).show()
        }
    }

    vivienda?.let {
        Column(modifier = modifier.padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
            Text(text = "MI VIVIENDA, aquí podrá añadir fotos o borrar la vivienda")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it.titulo)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Parroquia: ${it.parroquia}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it.descripcion)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Habitaciones: ${it.habitaciones}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tamaño: ${it.metros} m²")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Teléfono: ${it.telefono}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Email: ${it.email}")
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = "FOTOGRAFÍAS")
            Spacer(modifier = Modifier.height(15.dp))

            LazyRow {
                items(imageUris) { uri ->
                    Image(
                        painter = rememberImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { launcher.launch("image/*") }) {
                Text("Añadir Fotografías")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // TO DO: Implementar lógica para borrar anuncio
            }) {
                Text("Borrar Anuncio")
            }
        }
    } ?: run {
        Log.d("PantallaVivienda", "No se encontró la vivienda con id: $documentID")
    }
}

private fun subirFoto(uri: Uri, userID: String, documentID: String, storage: FirebaseStorage, context: Context, onSuccess: (Uri) -> Unit) {
    val storageRef = storage.reference
    val photoRef = storageRef.child("$userID/$documentID/${UUID.randomUUID()}.jpg")

    photoRef.putFile(uri)
        .addOnSuccessListener {
            photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                guardarFotoEnFirestore(downloadUri.toString(), userID, documentID)
                Toast.makeText(context, "Foto subida correctamente", Toast.LENGTH_SHORT).show()
                onSuccess(downloadUri)
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al subir la foto: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

private fun subirFoto(uri: Uri, userID: String, documentID: String, storage: FirebaseStorage, context: Context) {
    val storageRef = storage.reference
    val photoRef = storageRef.child("$userID/$documentID/${UUID.randomUUID()}.jpg")

    photoRef.putFile(uri)
        .addOnSuccessListener {
            photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                guardarFotoEnFirestore(downloadUri.toString(), userID, documentID)
                Toast.makeText(context, "Foto subida correctamente", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al subir la foto: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

private fun guardarFotoEnFirestore(url: String, userID: String, documentID: String) {
    val db = FirebaseFirestore.getInstance()
    val photoData = hashMapOf(
        "url" to url
    )
    db.collection("viviendas").document(documentID).collection("fotos").add(photoData)
}

suspend fun getMiViviendaById(id: String): Vivienda? {
    return try {
        FirebaseFirestore.getInstance()
            .collection("viviendas")
            .document(id)
            .get()
            .await()
            .toObject(Vivienda::class.java)
    } catch (e: Exception) {
        Log.e("PantallaVivienda", "Error al obtener la vivienda: ${e.message}")
        null
    }
}

suspend fun getFotosFromFirestore(documentID: String): List<Uri> {
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