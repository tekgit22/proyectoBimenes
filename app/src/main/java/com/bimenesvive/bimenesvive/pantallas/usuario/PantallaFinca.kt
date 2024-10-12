package com.bimenesvive.bimenesvive.pantallas.usuario

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.preference.PreferenceManager
import coil.compose.rememberAsyncImagePainter
import com.bimenesvive.bimenesvive.modelo.Finca
import com.bimenesvive.bimenesvive.utilidades.checkLocationPermission
import com.bimenesvive.bimenesvive.utilidades.saveFincaLocation
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.UUID
import com.bimenesvive.bimenesvive.utilidades.*

@Composable
fun PantallaFinca(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    documentID: String
) {
    var finca by remember { mutableStateOf<Finca?>(null) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showImageDialog by remember { mutableStateOf(false) }
    var showMapDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val userID = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current as Activity
    var ownerUserID by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState() // Para controlar el scroll de las imágenes

    // Solicitar permisos en tiempo de ejecución a usuarios con SO Android 13 +
    requestPhotoPermissions(context)

    LaunchedEffect(documentID) {
        try {
            finca = getFincaById(documentID)
            imageUris = getFotosFincaDesdeFirestore(documentID)
            ownerUserID = finca?.userID
            Log.d("PantallaFinca", "Finca: $finca")
        } catch (e: Exception) {
            Log.e("PantallaFinca", "Error al cargar la finca: ${e.message}")
            Toast.makeText(context, "Error al cargar la finca", Toast.LENGTH_SHORT).show()
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        uris.forEach { uri ->
            try {
                subirFotoFinca(
                    uri,
                    userID,
                    documentID,
                    FirebaseStorage.getInstance(),
                    context
                ) { downloadUri ->
                    imageUris = imageUris + downloadUri
                    // Actualizar la lista de fotos después de subirlas
                    coroutineScope.launch {
                        imageUris = getFotosFincaDesdeFirestore(documentID)
                    }
                }
            } catch (e: Exception) {
                Log.e("PantallaFinca", "Error al subir la foto: ${e.message}")
                Toast.makeText(context, "Error al subir la foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    finca?.let {
        Column(modifier = modifier.padding(16.dp)) {
            Text(text = it.titulo)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Parroquia: ${it.parroquia}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it.descripcion)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tamaño: ${it.metros} m²")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Teléfono: ${it.telefono}") //hacer clickable para llamar
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Email: ${it.email}") //hacer clickable para enviar email
            Spacer(modifier = Modifier.height(15.dp))

            Column {
                LazyRow(state = listState) {
                    items(imageUris) { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier
                                .size(128.dp)
                                .padding(4.dp)
                                .clickable {
                                    selectedImageUri = uri
                                    showImageDialog = true
                                }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (listState.firstVisibleItemIndex > 0) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(
                                            maxOf(listState.firstVisibleItemIndex - 1, 0)
                                        )
                                    }
                                },
                            tint = Color.Gray
                        )
                    } else {
                        Spacer(modifier = Modifier.size(24.dp))
                    }

                    val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                    if (lastVisibleItemIndex < imageUris.size - 1 && imageUris.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(
                                            minOf(listState.firstVisibleItemIndex + 1, imageUris.size - 1)
                                        )
                                    }
                                },
                            tint = Color.Gray
                        )
                    } else {
                        Spacer(modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                // si el userID == it.userID, crear boton para subir fotos
                if (userID == it.userID) {
                    Button(
                        onClick = {
                            launcher.launch("image/*")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Subir Foto")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                if (it.localizacion?.latitude == 0.0 && it.localizacion.longitude == 0.0) {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Localización no disponible")
                    }
                } else {
                    Button(
                        onClick = { showMapDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Ver Localización")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                if (userID == it.userID) {
                    Button(
                        onClick = {
                            // Verificar permisos de ubicación antes de guardar la localización
                            if (checkLocationPermission(context as Activity)) {
                                Log.d("PantallaFinca", "Permiso de ubicación concedido, guardando localización")
                                saveFincaLocation(it, context, documentID) {
                                    // Refrescar la UI después de guardar la localización
                                    coroutineScope.launch {
                                        finca = getFincaById(documentID)
                                    }
                                }
                            } else {
                                Log.d("PantallaFinca", "Permiso de ubicación no concedido")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Guardar Localización")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // El botón sólo existe para el usuario que creó el anuncio
            if (userID == it.userID) {
                // Botón para borrar anuncio
                Button(
                    onClick = {
                        try {
                            borrarAnuncio(
                                userID,
                                documentID,
                                imageUris,
                                context,
                                navController
                            )
                        } catch (e: Exception) {
                            Log.e("PantallaFinca", "Error al borrar el anuncio: ${e.message}")
                            Toast.makeText(context, "Error al borrar el anuncio", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Borrar Anuncio")
                }
            }

            if (showImageDialog) {
                AlertDialog(
                    onDismissRequest = { showImageDialog = false },
                    text = {
                        selectedImageUri?.let { uri ->
                            RotarImagen(imageUri = uri)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showImageDialog = false }) {
                            Text("Cerrar")
                        }
                    }
                )
            }

            if (showMapDialog) {
                AlertDialog(
                    onDismissRequest = { showMapDialog = false },
                    text = {
                        AndroidView(factory = { context ->
                            MapView(context).apply {
                                Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
                                controller.setZoom(18.0)
                                finca?.localizacion?.let { geoPoint ->
                                    val startPoint = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                                    controller.setCenter(startPoint)
                                    val marker = Marker(this)
                                    marker.position = startPoint
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    overlays.add(marker)
                                }
                            }
                        })
                    },
                    confirmButton = {
                        TextButton(onClick = { showMapDialog = false }) {
                            Text("Cerrar")
                        }
                    }
                )
            }
        }
    } ?: run {
        // Log sólo para comprobar errores cambiando los modelos, da falsos positivos porque necesitamos
        // tener en cuenta el tiempo de carga de Firestore, por lo que comento sólo para usarlo si es necesario

       // Log.d("PantallaFinca", "No se encontró la finca con id: $documentID")
    }
}

private const val REQUEST_CODE_SELECT_IMAGE = 1001

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




suspend fun getFincaById(id: String): Finca? {
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

