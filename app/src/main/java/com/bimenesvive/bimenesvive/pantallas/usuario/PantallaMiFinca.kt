package com.bimenesvive.bimenesvive.pantallas.usuario

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.bimenesvive.bimenesvive.modelo.Finca
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PantallaMiFinca(modifier: Modifier, navController: NavController, documentID: String) {
    val finca = remember { mutableStateOf<Finca?>(null) }
    val fotos = remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current as Activity
    val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return

    LaunchedEffect(documentID) {
        finca.value = getMiFincaById(documentID)
        fotos.value = getFotosFincaFromFirestore(documentID)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        uris.forEach { uri ->
            try {
                subirFotoFinca(uri, userID, documentID, FirebaseStorage.getInstance(), context) { downloadUri ->
                    fotos.value = fotos.value + downloadUri
                }
            } catch (e: Exception) {
                Log.e("PantallaFinca", "Error al subir la foto: ${e.message}")
                Toast.makeText(context, "Error al subir la foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "MI FINCA")
        Spacer(modifier = Modifier.height(16.dp))

        finca.value?.let {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = it.titulo, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = it.descripcion, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = it.parroquia, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "${it.metros} metros cuadrados", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = if (it.edificable) "Edificable" else "No Edificable", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = it.telefono, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = it.email, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Carrusel de fotos
            LazyRow {
                items(fotos.value) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(128.dp)
                            .padding(4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Botones
            Button(onClick = { launcher.launch("image/*") }) {
                Text("Añadir Fotografías")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                try {
                    borrarAnuncio(userID, documentID, fotos.value, context, navController)
                } catch (e: Exception) {
                    Log.e("PantallaFinca", "Error al borrar el anuncio: ${e.message}")
                    Toast.makeText(context, "Error al borrar el anuncio: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Borrar Anuncio")
            }
        }
    }
}

private fun borrarAnuncio(userID: String, documentID: String, imageUris: List<Uri>, context: Context, navController: NavController) {
    val storageRef = FirebaseStorage.getInstance().reference
    val db = FirebaseFirestore.getInstance()

    if (imageUris.isNotEmpty()) {
        val deleteTasks = imageUris.map { uri ->
            val fileName = uri.lastPathSegment?.substringAfterLast('/')
            val photoRef = storageRef.child("$userID/$documentID/$fileName")
            photoRef.delete().addOnFailureListener { e ->
                val errorMessage = if (e.message?.contains("Object does not exist") == true) {
                    "La foto no existe en la ubicación especificada"
                } else {
                    "Error al borrar la foto: ${e.message}"
                }
                Log.e("PantallaFinca", errorMessage)
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        Tasks.whenAll(deleteTasks)
            .addOnSuccessListener {
                db.collection("fincas").document(documentID).collection("fotos")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val deletePhotoDocsTasks = snapshot.documents.map { it.reference.delete() }
                        Tasks.whenAll(deletePhotoDocsTasks)
                            .addOnSuccessListener {
                                db.collection("fincas").document(documentID)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Anuncio borrado correctamente", Toast.LENGTH_SHORT).show()
                                        navController.navigate("inicio")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("PantallaFinca", "Error al borrar el anuncio: ${e.message}")
                                        Toast.makeText(context, "Error al borrar el anuncio: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e("PantallaFinca", "Error al borrar los documentos de las fotos: ${e.message}")
                                Toast.makeText(context, "Error al borrar los documentos de las fotos: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("PantallaFinca", "Error al obtener los documentos de las fotos: ${e.message}")
                        Toast.makeText(context, "Error al obtener los documentos de las fotos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("PantallaFinca", "Error al borrar las fotos: ${e.message}")
                Toast.makeText(context, "Error al borrar las fotos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    } else {
        db.collection("fincas").document(documentID)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Anuncio borrado correctamente", Toast.LENGTH_SHORT).show()
                navController.navigate("inicio")
            }
            .addOnFailureListener { e ->
                Log.e("PantallaFinca", "Error al borrar el anuncio: ${e.message}")
                Toast.makeText(context, "Error al borrar el anuncio: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

private fun borrarFinca(documentID: String, context: Context, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val fincaRef = db.collection("fincas").document(documentID)

    fincaRef.collection("fotos").get()
        .addOnSuccessListener { snapshot ->
            val deletePhotoDocsTasks = snapshot.documents.map { it.reference.delete() }
            Tasks.whenAll(deletePhotoDocsTasks)
                .addOnSuccessListener {
                    // Después de eliminar las fotos y sus documentos, eliminar el documento principal
                    fincaRef.delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Finca borrada correctamente", Toast.LENGTH_SHORT).show()
                            navController.navigate("inicio")
                        }
                        .addOnFailureListener { e ->
                            Log.e("PantallaFinca", "Error al borrar la finca: ${e.message}")
                            Toast.makeText(context, "Error al borrar la finca: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("PantallaFinca", "Error al borrar los documentos de las fotos: ${e.message}")
                    Toast.makeText(context, "Error al borrar los documentos de las fotos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        .addOnFailureListener { e ->
            Log.e("PantallaFinca", "Error al obtener los documentos de las fotos: ${e.message}")
            Toast.makeText(context, "Error al obtener los documentos de las fotos: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

private fun subirFotoFinca(uri: Uri, userID: String, documentID: String, storage: FirebaseStorage, context: Context, onSuccess: (Uri) -> Unit) {
    val storageRef = storage.reference
    val photoRef = storageRef.child("$userID/$documentID/${UUID.randomUUID()}.jpg")

    photoRef.putFile(uri)
        .addOnSuccessListener {
            photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                guardarFotoFincaEnFirestore(downloadUri.toString(), userID, documentID)
                Toast.makeText(context, "Foto subida correctamente", Toast.LENGTH_SHORT).show()
                onSuccess(downloadUri)
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al subir la foto: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

private fun guardarFotoFincaEnFirestore(url: String, userID: String, documentID: String) {
    val db = FirebaseFirestore.getInstance()
    val photoData = hashMapOf(
        "url" to url
    )
    db.collection("fincas").document(documentID).collection("fotos").add(photoData)
}

suspend fun getMiFincaById(id: String): Finca? {
    return try {
        FirebaseFirestore.getInstance()
            .collection("fincas")
            .document(id)
            .get()
            .await()
            .toObject(Finca::class.java)
    } catch (e: Exception) {
        Log.e("PantallaFinca", "Error al obtener la finca: ${e.message}")
        null
    }
}

suspend fun getFotosFincaFromFirestore(documentID: String): List<Uri> {
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