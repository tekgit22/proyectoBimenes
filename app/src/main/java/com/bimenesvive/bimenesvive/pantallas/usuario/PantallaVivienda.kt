package com.bimenesvive.bimenesvive.pantallas.usuario

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Looper
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.bimenesvive.bimenesvive.modelo.Vivienda
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage

private const val LOCATION_PERMISSION_REQUEST_CODE = 1

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
    val listState = rememberLazyListState()

    // Inicializar osmdroid
    val context = LocalContext.current
    Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

    LaunchedEffect(documentID) {
        vivienda = getViviendaById(documentID)
        imageUris = getFotosViviendaFromFirestore(documentID)
        Log.d("PantallaVivienda", "Vivienda: $vivienda")
    }

    vivienda?.let {
        Column(modifier = modifier.padding(16.dp)) {
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
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

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
                                Log.d("PantallaVivienda", "Permiso de ubicación concedido, guardando localización")
                                saveLocation(it, context, documentID) {
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

            // Botón para borrar anuncio
            if (userID == it.userID) {
                Button(
                    onClick = {
                        try {
                            borrarAnuncio(userID, documentID, imageUris, context, navController)
                        } catch (e: Exception) {
                            Log.e("PantallaVivienda", "Error al borrar el anuncio: ${e.message}")
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
                                vivienda?.localizacion?.let { geoPoint ->
                                    val startPoint = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                                    controller.setCenter(startPoint)
                                    val marker = Marker(this)
                                    marker.position = startPoint
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    marker.setOnMarkerClickListener { _, _ ->
                                        Toast.makeText(context, "Localización: ${geoPoint.latitude}, ${geoPoint.longitude}", Toast.LENGTH_SHORT).show()
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

fun checkLocationPermission(activity: Activity): Boolean {
    return if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        false
    } else {
        true
    }
}

fun getCurrentLocation(activity: Activity, onLocationReceived: (Location) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    val locationRequest = LocationRequest.Builder(10000)
        .setMinUpdateIntervalMillis(5000)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .build()

    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        return
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                onLocationReceived(location)
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }, Looper.getMainLooper())
}

fun saveLocation(vivienda: Vivienda, context: Activity, documentID: String, onLocationSaved: (Location) -> Unit) {
    if (checkLocationPermission(context)) {
        getCurrentLocation(context) { location ->
            val db = FirebaseFirestore.getInstance()
            val viviendaRef = db.collection("viviendas").document(documentID)
            val geoPoint = com.google.firebase.firestore.GeoPoint(location.latitude, location.longitude)

            Log.d("PantallaVivienda", "Guardando localización: lat=${location.latitude}, lon=${location.longitude}")
            Toast.makeText(context, "lat=${location.latitude}, lon=${location.longitude}", Toast.LENGTH_SHORT).show()

            val data = mapOf("localizacion" to geoPoint)
            viviendaRef.update(data)
                .addOnSuccessListener {
                    Log.d("PantallaVivienda", "Localización guardada exitosamente en Firestore")
                    Toast.makeText(context, "Localización guardada exitosamente", Toast.LENGTH_SHORT).show()
                    onLocationSaved(location)
                }
                .addOnFailureListener { e ->
                    Log.e("PantallaVivienda", "Error al guardar la localización en Firestore", e)
                    Toast.makeText(context, "Error al guardar la localización", Toast.LENGTH_SHORT).show()
                }
        }
    } else {
        Log.d("PantallaVivienda", "Permiso de ubicación no concedido")
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

@Composable
fun RotarImagen(imageUri: Uri, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(model = imageUri)

    var rotation by remember { mutableStateOf(0f) }
    var contentScale by remember { mutableStateOf(ContentScale.Fit) }

    LaunchedEffect(painter.state) {
        when (painter.state) {
            is AsyncImagePainter.State.Success -> {
                val imageSize = (painter.state as AsyncImagePainter.State.Success).painter.intrinsicSize
                if (imageSize.width > imageSize.height) {
                    rotation = 90f
                    contentScale = ContentScale.Crop
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