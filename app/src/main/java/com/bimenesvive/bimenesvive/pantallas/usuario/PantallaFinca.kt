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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.preference.PreferenceManager
import coil.compose.rememberAsyncImagePainter
import com.bimenesvive.bimenesvive.modelo.EstadoPropiedad
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

    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = { NavBarViendoPropiedad(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplicar paddingValues aquí
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
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
                        text = finca?.visitas?.toString() ?: "0",
                        color = Color.Gray
                    )
                }
                Text(
                    text = finca?.titulo ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = finca?.descripcion ?: "")
                AssistChip(
                    onClick = {},
                    label = { Text(finca?.parroquia ?: "") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            modifier = Modifier.size(AssistChipDefaults.IconSize)
                        )
                    },
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    finca?.let {
                        when (it.estado) {
                            EstadoPropiedad.VENTA -> {
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
                                )
                            }

                            EstadoPropiedad.ALQUILER -> {
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
                                )
                            }

                            EstadoPropiedad.ALQUILER_Y_VENTA -> {
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
                                    modifier = Modifier.padding(end = 8.dp)
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
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text("${finca?.metros} m²") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.SquareFoot,
                                contentDescription = null,
                                modifier = Modifier.size(AssistChipDefaults.IconSize)
                            )
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text("Edificable: ${if (finca?.edificable == true) "Sí" else "No"}") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                modifier = Modifier.size(AssistChipDefaults.IconSize)
                            )
                        },
                    )
                }

                Column {
                    if (finca?.precio == 0) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Precio no disponible") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Euro,
                                    contentDescription = null,
                                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                                )
                            },
                        )
                    } else {
                        AssistChip(
                            onClick = {},
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Precio: ${finca?.precio} ")
                                    Icon(
                                        imageVector = Icons.Default.Euro,
                                        contentDescription = null,
                                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                                    )
                                }
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Teléfono",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = finca?.telefono ?: "",
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            val telefonoPrefijoSpain = "+34${finca?.telefono}"
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
                        text = finca?.email ?: "",
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            val email = finca?.email ?: return@clickable
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$email")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
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
                    if (userID == finca?.userID) {
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
                    if (finca?.localizacion?.latitude == 0.0 && finca?.localizacion?.longitude == 0.0) {
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
                    if (userID == finca?.userID) {
                        Button(
                            onClick = {
                                if (checkLocationPermission(context as Activity)) {
                                    saveFincaLocation(finca!!, context, documentID) {
                                        coroutineScope.launch {
                                            finca = getFincaById(documentID)
                                        }
                                    }
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

                if (userID == finca?.userID) {
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
                                    finca?.localizacion?.let { geoPoint ->
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



        // Gradiente en la parte inferior
            if (scrollState.value < scrollState.maxValue) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                            )
                        )
                )
            }
        }
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

