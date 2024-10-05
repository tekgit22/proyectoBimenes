package com.bimenesvive.bimenesvive.pantallas.usuario

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.bimenesvive.bimenesvive.modelo.Vivienda
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import androidx.preference.PreferenceManager

@Composable
fun PantallaVivienda(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    documentID: String
) {
    var vivienda by remember { mutableStateOf<Vivienda?>(null) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }

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

            LazyRow {
                items(imageUris) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(128.dp)
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(onClick = { showDialog = true }) {
                Text("Ver Localización")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    text = {
                        AndroidView(factory = { context ->
                            MapView(context).apply {
                                setMultiTouchControls(true)
                                controller.setZoom(19.0)
                                it.localizacion?.let { loc ->
                                    val geoPoint = GeoPoint(loc.latitude, loc.longitude)
                                    controller.setCenter(geoPoint)

                                    // Añadir un marcador
                                    val marker = Marker(this)
                                    marker.position = geoPoint
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    marker.title = it.titulo
//                                    marker.setOnMarkerClickListener { _, _ ->
//                                        val gmmIntentUri = Uri.parse("geo:${loc.latitude},${loc.longitude}")
//                                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//                                        mapIntent.setPackage("com.google.android.apps.maps")
//                                        context.startActivity(mapIntent)
//                                        true
//                                    }
                                    overlays.add(marker)
                                }
                                // Añadir un proveedor de tiles
                                val tileProvider = org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK
                                setTileSource(tileProvider)
                            }
                        }, modifier = Modifier.fillMaxSize())
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cerrar")
                        }
                    }
                )
            }
        }
    } ?: run {
        Log.d("PantallaVivienda", "No se encontró la vivienda con id: $documentID")
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
