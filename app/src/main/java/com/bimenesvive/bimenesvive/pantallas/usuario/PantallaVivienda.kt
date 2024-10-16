package com.bimenesvive.bimenesvive.pantallas.usuario

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.bimenesvive.bimenesvive.modelo.Vivienda
import com.bimenesvive.bimenesvive.utilidades.*
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextDecoration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.bimenesvive.bimenesvive.utilidades.*

import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.preference.PreferenceManager
import coil.compose.AsyncImagePainter
import com.bimenesvive.bimenesvive.modelo.EstadoVivienda


@Composable
fun PantallaVivienda(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    documentID: String
) {
    var vivienda by remember { mutableStateOf<Vivienda?>(null) }
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
        vivienda = getViviendaById(documentID)
        imageUris = getFotosViviendaDesdeFirestore(documentID)
    }

    LaunchedEffect(documentID) {
        try {
            vivienda = getViviendaById(documentID)
            imageUris =
                com.bimenesvive.bimenesvive.utilidades.getFotosViviendaDesdeFirestore(documentID)
            ownerUserID = vivienda?.userID
            Log.d("PantallaVivienda", "Vivienda: $vivienda")
        } catch (e: Exception) {
            Log.e("PantallaVivienda", "Error al cargar la vivienda: ${e.message}")
            Toast.makeText(context, "Error al cargar la vivienda", Toast.LENGTH_SHORT).show()
        }
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            uris.forEach { uri ->
                try {
                    subirFotoVivienda(
                        uri,
                        userID,
                        documentID,
                        FirebaseStorage.getInstance(),
                        context
                    ) { downloadUri ->
                        imageUris = imageUris + downloadUri
                    }
                } catch (e: Exception) {
                    Log.e("PantallaVivienda", "Error al subir la foto: ${e.message}")
                    Toast.makeText(context, "Error al subir la foto", Toast.LENGTH_SHORT).show()
                }
            }
        }

    Scaffold(
        bottomBar = { NavBarViendoPropiedad(navController) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Visitas",
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = vivienda?.visitas?.toString() ?: "0",
                    color = Color.Gray
                )
            }
            Text(
                text = vivienda?.titulo ?: "",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Ubicado en ${vivienda?.parroquia}")
            // CHIPS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                vivienda?.let {
                    when (it.estado) {
                        EstadoVivienda.VENTA -> {
                            AssistChip(
                                onClick = {},
                                label = { Text("Se vende") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = null,
                                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                                    )
                                },
                                modifier = Modifier.padding(4.dp)
                            )
                        }

                        EstadoVivienda.ALQUILER -> {
                            AssistChip(
                                onClick = {},
                                label = { Text("Se alquila") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Key,
                                        contentDescription = null,
                                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                                    )
                                },
                                modifier = Modifier.padding(4.dp)
                            )
                        }

                        EstadoVivienda.ALQUILER_Y_VENTA -> {
                            AssistChip(
                                onClick = {},
                                label = { Text("Se vende") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = null,
                                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                                    )
                                },
                                modifier = Modifier.padding(4.dp)
                            )
                            AssistChip(
                                onClick = {},
                                label = { Text("Se alquila") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Key,
                                        contentDescription = null,
                                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                                    )
                                },
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = vivienda?.descripcion ?: "")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tamaño: ${vivienda?.metros} m²")
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Teléfono",
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = vivienda?.telefono ?: "",
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        val telefonoPrefijoSpain = "+34${vivienda?.telefono}"
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$telefonoPrefijoSpain")
                        }
                        context.startActivity(intent)
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = vivienda?.email ?: "",
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        val email = vivienda?.email ?: return@clickable
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$email")
                        }
                        context.startActivity(intent)
                    }
                )
            }
            Spacer(modifier = Modifier.height(15.dp))

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

                    val lastVisibleItemIndex =
                        listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                    if (lastVisibleItemIndex < imageUris.size - 1) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(
                                            minOf(
                                                listState.firstVisibleItemIndex + 1,
                                                imageUris.size - 1
                                            )
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
                if (userID == vivienda?.userID) {
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
                if (vivienda?.localizacion?.latitude == 0.0 && vivienda?.localizacion?.longitude == 0.0) {
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
                if (userID == vivienda?.userID) {
                    Button(
                        onClick = {
                            // Verificar permisos de ubicación antes de guardar la localización
                            if (checkLocationPermission(context as Activity)) {
                                Log.d(
                                    "PantallaVivienda",
                                    "Permiso de ubicación concedido, guardando localización"
                                )
                                saveViviendaLocation(vivienda!!, context, documentID) {
                                    // Refrescar la UI después de guardar la localización
                                    coroutineScope.launch {
                                        vivienda = getViviendaById(documentID)
                                    }
                                }
                            } else {
                                Log.d("PantallaVivienda", "Permiso de ubicación no concedido")
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
            if (userID == vivienda?.userID) {
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
                            Log.e("PantallaVivienda", "Error al borrar el anuncio: ${e.message}")
                            Toast.makeText(
                                context,
                                "Error al borrar el anuncio",
                                Toast.LENGTH_SHORT
                            ).show()
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
                                Configuration.getInstance().load(
                                    context,
                                    PreferenceManager.getDefaultSharedPreferences(context)
                                )
                                controller.setZoom(18.0)
                                vivienda?.localizacion?.let { geoPoint ->
                                    val startPoint = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                                    controller.setCenter(startPoint)
                                    val marker = Marker(this)
                                    marker.position = startPoint
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    marker.setOnMarkerClickListener { _, _ ->
                                        Toast.makeText(
                                            context,
                                            "Localización: ${geoPoint.latitude}, ${geoPoint.longitude}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        true
                                    }
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

        // Log.d("PantallaVivienda", "No se encontró la vivienda con id: $documentID")
    }
}


@Composable
fun RotarImagen(imageUri: Uri, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(model = imageUri)

    var rotation by remember { mutableStateOf(0f) }
    var contentScale by remember { mutableStateOf(ContentScale.Fit) }

    LaunchedEffect(painter.state) {
        when (painter.state) {
            is AsyncImagePainter.State.Success -> {
                val imageSize =
                    (painter.state as AsyncImagePainter.State.Success).painter.intrinsicSize
                if (imageSize.width > imageSize.height) {
                    rotation = 90f
                    contentScale = ContentScale.FillWidth
                } else {
                    contentScale = ContentScale.FillHeight
                }
            }

            else -> {}
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = contentScale,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    rotationZ = rotation,
                    scaleX = if (rotation != 0f) 1.5f else 1f,
                    scaleY = if (rotation != 0f) 1.5f else 1f
                )
        )
    }
}

private fun borrarAnuncio(
    userID: String,
    documentID: String,
    imageUris: List<Uri>,
    context: Context,
    navController: NavHostController
) {
    val storageRef = FirebaseStorage.getInstance().reference
    val db = FirebaseFirestore.getInstance()

    if (imageUris.isNotEmpty()) {
        val deleteTasks = imageUris.map { uri ->
            val fileName = uri.lastPathSegment?.substringAfterLast('/')
            val photoRef = storageRef.child("$userID/$documentID/$fileName")
            photoRef.delete().addOnFailureListener { e ->
                val errorMessage = if (e.message?.contains("Object does not exist") == true) {
                    "La foto no existe en el almacenamiento"
                } else {
                    "Error al borrar la foto: ${e.message}"
                }
                Log.e("PantallaVivienda", errorMessage)
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        Tasks.whenAll(deleteTasks)
            .addOnSuccessListener {
                db.collection("viviendas").document(documentID).collection("fotos")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val deletePhotoDocsTasks =
                            querySnapshot.documents.map { it.reference.delete() }
                        Tasks.whenAll(deletePhotoDocsTasks)
                            .addOnSuccessListener {
                                db.collection("viviendas").document(documentID)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Anuncio borrado correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("inicio")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(
                                            "PantallaVivienda",
                                            "Error al borrar el anuncio: ${e.message}"
                                        )
                                        Toast.makeText(
                                            context,
                                            "Error al borrar el anuncio: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e(
                                    "PantallaVivienda",
                                    "Error al borrar los documentos de las fotos: ${e.message}"
                                )
                                Toast.makeText(
                                    context,
                                    "Error al borrar los documentos de las fotos: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e(
                            "PantallaVivienda",
                            "Error al obtener los documentos de las fotos: ${e.message}"
                        )
                        Toast.makeText(
                            context,
                            "Error al obtener los documentos de las fotos: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("PantallaVivienda", "Error al borrar las fotos: ${e.message}")
                Toast.makeText(
                    context,
                    "Error al borrar las fotos: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    } else {
        db.collection("viviendas").document(documentID)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Anuncio borrado correctamente", Toast.LENGTH_SHORT).show()
                navController.navigate("inicio")
            }
            .addOnFailureListener { e ->
                Log.e("PantallaVivienda", "Error al borrar el anuncio: ${e.message}")
                Toast.makeText(
                    context,
                    "Error al borrar el anuncio: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}

suspend fun getViviendaById(id: String): Vivienda? {
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










