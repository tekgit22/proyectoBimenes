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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.bimenesvive.bimenesvive.modelo.Vivienda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*
import com.google.android.gms.tasks.Tasks

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
        try {
            vivienda = getMiViviendaById(documentID)
            imageUris = getFotosFromFirestore(documentID)
            Log.d("PantallaVivienda", "Vivienda: $vivienda")
        } catch (e: Exception) {
            Log.e("PantallaVivienda", "Error al cargar la vivienda: ${e.message}")
            Toast.makeText(context, "Error al cargar la vivienda", Toast.LENGTH_SHORT).show()
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        uris.forEach { uri ->
            try {
                subirFoto(uri, userID, documentID, FirebaseStorage.getInstance(), context) { downloadUri ->
                    imageUris = imageUris + downloadUri
                }
            } catch (e: Exception) {
                Log.e("PantallaVivienda", "Error al subir la foto: ${e.message}")
                Toast.makeText(context, "Error al subir la foto", Toast.LENGTH_SHORT).show()
            }
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
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier.size(128.dp).padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { launcher.launch("image/*") }) {
                Text("Añadir Fotografías")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                try {
                    borrarAnuncio(userID, documentID, imageUris, context, navController)
                } catch (e: Exception) {
                    Log.e("PantallaVivienda", "Error al borrar el anuncio: ${e.message}")
                    Toast.makeText(context, "Error al borrar el anuncio", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Borrar Anuncio")
            }
        }
    } ?: run {
        Log.d("PantallaVivienda", "No se encontró la vivienda con id: $documentID")
    }
}

fun borrarAnuncio(userID: String, documentID: String, imageUris: List<Uri>, context: Context, navController: NavHostController) {
    val storageRef = FirebaseStorage.getInstance().reference
    val db = FirebaseFirestore.getInstance()

    // Eliminar las fotos de Storage si existen
    if (imageUris.isNotEmpty()) {
        val deleteTasks = imageUris.map { uri ->
            // Extract only the file name from the URI
            val fileName = uri.lastPathSegment?.substringAfterLast('/')
            val photoRef = storageRef.child("$userID/$documentID/$fileName")
            Log.d("PantallaVivienda", "userID: $userID")
            Log.d("PantallaVivienda", "documentID: $documentID")
            Log.d("PantallaVivienda", "fileName: $fileName")
            Log.d("PantallaVivienda", "Ruta de la foto a borrar: ${photoRef.path}")
            photoRef.delete().addOnFailureListener { e ->
                val errorMessage = if (e.message?.contains("Object does not exist") == true) {
                    "La foto no existe en la ubicación especificada"
                } else {
                    "Error al borrar la foto: ${e.message}"
                }
                Log.e("PantallaVivienda", errorMessage)
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        Tasks.whenAll(deleteTasks)
            .addOnSuccessListener {
                // Eliminar los documentos de las URIs de las fotos en Firestore
                db.collection("viviendas").document(documentID).collection("fotos")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val deletePhotoDocsTasks = snapshot.documents.map { it.reference.delete() }
                        Tasks.whenAll(deletePhotoDocsTasks)
                            .addOnSuccessListener {
                                // Después de eliminar las fotos y sus documentos, eliminar el documento principal
                                db.collection("viviendas").document(documentID)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Anuncio borrado correctamente", Toast.LENGTH_SHORT).show()
                                        navController.navigate("inicio")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("PantallaVivienda", "Error al borrar el anuncio: ${e.message}")
                                        Toast.makeText(context, "Error al borrar el anuncio: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e("PantallaVivienda", "Error al borrar los documentos de las fotos: ${e.message}")
                                Toast.makeText(context, "Error al borrar los documentos de las fotos: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("PantallaVivienda", "Error al obtener los documentos de las fotos: ${e.message}")
                        Toast.makeText(context, "Error al obtener los documentos de las fotos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("PantallaVivienda", "Error al borrar las fotos: ${e.message}")
                Toast.makeText(context, "Error al borrar las fotos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    } else {
        // Si no hay fotos, eliminar directamente el documento principal
        db.collection("viviendas").document(documentID)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Anuncio borrado correctamente", Toast.LENGTH_SHORT).show()
                navController.navigate("inicio")
            }
            .addOnFailureListener { e ->
                Log.e("PantallaVivienda", "Error al borrar el anuncio: ${e.message}")
                Toast.makeText(context, "Error al borrar el anuncio: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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