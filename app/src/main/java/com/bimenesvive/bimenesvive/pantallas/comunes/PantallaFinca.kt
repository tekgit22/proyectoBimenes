package com.bimenesvive.bimenesvive.pantallas.comunes

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.bimenesvive.bimenesvive.componentes.ImagenRotable
import com.bimenesvive.bimenesvive.componentes.NavBarViendoPropiedad
import com.bimenesvive.bimenesvive.modelos.EstadoPropiedad
import com.bimenesvive.bimenesvive.modelos.Finca
import com.bimenesvive.bimenesvive.utilidades.checkLocationPermission
import com.bimenesvive.bimenesvive.utilidades.saveFincaLocation
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.bimenesvive.bimenesvive.utilidades.*
import android.provider.Settings

@Composable
fun PantallaFinca(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    documentID: String
) {
    var finca by remember { mutableStateOf<Finca?>(null) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var imagenSeleccionada by remember { mutableStateOf<Uri?>(null) }
    var esImagenGrande by remember { mutableStateOf(false) }
    val userID = FirebaseAuth.getInstance().currentUser?.uid ?: "invitado"
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current as Activity
    var ownerUserID by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState() // Almacena el estado de scroll de la lista de imágenes
    val scrollState = rememberScrollState() // Almacena el estado del scroll,
    // se utiliza para mostrar un gradiente en la parte inferior

    /**
     * Cargar la finca, las fotos y el ID del propietario
     * @param documentID ID del documento de la finca en Firestore
     */
    LaunchedEffect(documentID) {
        try {
            finca = cargarFincaPorID(documentID)
            imageUris = obtenerUrisFotosFinca(documentID)
            ownerUserID = finca?.userID
            Log.d("PantallaFinca", "Finca: $finca")
        } catch (e: Exception) {
            Log.e("PantallaFinca", "Error al cargar la finca: ${e.message}")
            Toast.makeText(context, "Error al cargar la finca", Toast.LENGTH_SHORT).show()
        }
    }

    // ActivityResultLauncher para lanzar un selector de imágenes de la galería
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            uris.forEach { uri ->
                try {
                    subirFotoFinca(
                        uri,
                        userID,
                        documentID,
                        FirebaseStorage.getInstance(),
                        context
                    ) { downloadUri -> // Callback si se hizo correctamente la subida
                        imageUris = imageUris + downloadUri
                        // Actualizar la lista de fotos después de subirlas para que se muestren en la UI
                        coroutineScope.launch {
                            imageUris = obtenerUrisFotosFinca(documentID)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PantallaFinca", "Error al subir la foto: ${e.message}")
                    Toast.makeText(context, "Error al subir la foto", Toast.LENGTH_SHORT).show()
                }
            }
        }

    Scaffold(
        bottomBar = { NavBarViendoPropiedad(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // La UI mostrará la imagen seleccionada a pantalla completa
            if (esImagenGrande && imagenSeleccionada != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    ImagenRotable(
                        imageUri = imagenSeleccionada!!,
                        modifier = Modifier.fillMaxSize()
                    )
//                    Image(
//                        painter = rememberAsyncImagePainter(imagenSeleccionada),
//                        contentDescription = null,
//                        modifier = Modifier.fillMaxSize()
//                    )
                    IconButton(
                        onClick = { esImagenGrande = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color.LightGray, shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White
                        )
                    }
                }
            } else {
                // CONTENIDO DE LA PANTALLA
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
                        label = { Text(finca?.ubicacion ?: "") },
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
                            label = { Text(if (finca?.edificable == true) "Edificable" else "No edificable") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                                )
                            },
                        )
                    }
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
                                    //data es una propiedad de objeto Intent que se utiliza para almacenar una URI
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
                    Spacer(modifier = Modifier.height(16.dp))

                    //LazyRow que hace el display de las imágenes redondas
                    LazyRow(state = listState) {
                        items(imageUris) { uri ->
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(
                                        LocalContext.current
                                    ).data(data = uri)
                                        .apply(block = fun ImageRequest.Builder.() {
                                            transformations(CircleCropTransformation())
                                        }).build()
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(128.dp)
                                    .padding(4.dp)
                                    .clickable {
                                        imagenSeleccionada = uri
                                        esImagenGrande = true
                                    }
                            )
                        }
                    }

                    //Row encargada de los Iconos de flechas para moverse entre las imágenes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (listState.firstVisibleItemIndex > 0) {
                            // Si no es la primera imagen, muestra el icon de flecha a la izquierda
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Moverse al anterior elemento",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(
                                                maxOf(listState.firstVisibleItemIndex - 1, 0)
                                                //maxOf para evitar valores negativos (cálculo, mínimo)
                                                //0 es el valor mínimo permitido para ese índice
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
                        // lastOrNull() devuelve el último elemento de la lista o null si está vacía
                        // en caso de ser null, le asigno un -1
                        if (lastVisibleItemIndex < imageUris.size - 1 && imageUris.isNotEmpty()) {
                            // Si la última foto visible es menor que el tamaño de la lista, muestra el Icon de flecha a la derecha
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Moverse al elemento posterior",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(
                                                minOf(
                                                    listState.firstVisibleItemIndex + 1, // Siguiente índice
                                                    imageUris.size - 1 // Último índice permitido
                                                )
                                            )
                                        }
                                    },
                                tint = Color.Gray
                            )
                        } else {
                            // Spacer(modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Botones
                    if (userID == finca?.userID) {
                        //Subir Foto y Guardar Localización
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    requestPhotoPermissions(context)
                                    launcher.launch("image/*") // Abre una instancia de ActivityResultLauncher con un selector de imágenes
                                },
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Icono subir foto"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Subir")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    // Verificar si el dispositivo tiene GPS
                                    if (tieneGPS(context)) {
                                        // Verificar permisos de ubicación antes de guardar la localización
                                        if (checkLocationPermission(context)) {
                                            if (isGPSEnabled(context)) {
                                                Log.d(
                                                    "PantallaFinca",
                                                    "Permiso de ubicación concedido y GPS activado, guardando localización"
                                                )
                                                saveFincaLocation(finca!!, context, documentID) {
                                                    // Refrescar la UI después de guardar la localización
                                                    coroutineScope.launch {
                                                        finca = cargarFincaPorID(documentID)
                                                    }
                                                }
                                            } else {
                                                Log.d(
                                                    "PantallaFinca",
                                                    "GPS no activado, solicitando activación"
                                                )
                                                val intent =
                                                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                                context.startActivity(intent)
                                            }
                                        } else {
                                            Log.d(
                                                "PantallaFinca",
                                                "Permiso de ubicación no concedido"
                                            )
                                        }
                                    } else {
                                        Log.d(
                                            "PantallaFinca",
                                            "El dispositivo no tiene funcionalidad GPS"
                                        )
                                        Toast.makeText(
                                            context,
                                            "El dispositivo no tiene funcionalidad GPS",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddLocationAlt,
                                    contentDescription = "Icono guardar localización"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardar")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón de ver localización o localización no disponible
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
                            onClick = {
                                finca?.localizacion?.let { geoPoint ->
                                    val googleMapsIntentUri =
                                        Uri.parse("geo:${geoPoint.latitude},${geoPoint.longitude}?q=${geoPoint.latitude},${geoPoint.longitude}(${finca?.titulo})")
                                    val mapIntent =
                                        Intent(Intent.ACTION_VIEW, googleMapsIntentUri).apply {
                                            setPackage("com.google.android.apps.maps") // Especifico que tiene que ser abierto con Google Maps
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // Añado una flag para abrir en una nueva tarea para la actividad
                                            // Esta flag no es obligatoria porque la abro desde un composable, pero es buena práctica (obligatoria en services o broadcast receivers)
                                        }
                                    context.startActivity(mapIntent)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text("Ver Localización")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    //Transado y Borrar Anuncio
                    if (userID == finca?.userID) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            fincaTransada(documentID)
                                            Toast.makeText(
                                                context,
                                                "El anuncio se ha archivado",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.popBackStack() // Borro la pantalla actual de la pila de navegación
                                            navController.navigate("inicio")
                                        } catch (e: Exception) {
                                            Log.e(
                                                "PantallaFinca",
                                                "Error al cambiar disponibilidad: ${e.message}"
                                            )
                                            Toast.makeText(
                                                context,
                                                "Error al marcar como transada",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Euro,
                                    contentDescription = "Símbolo €"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Transado")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    try {
                                        borrarAnuncioFinca(
                                            userID,
                                            documentID,
                                            imageUris,
                                            context,
                                            navController
                                        )
                                    } catch (e: Exception) {
                                        Log.e(
                                            "PantallaFinca",
                                            "Error al borrar el anuncio: ${e.message}"
                                        )
                                        Toast.makeText(
                                            context,
                                            "Error al borrar el anuncio: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF44336)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Icono papelera"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Borrar")
                            }
                        }
                    }
                } // Final de la LazyColumn

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
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}


/**
 * Borra un anuncio de la base de datos de Firestore
 * @param userID ID del usuario actual
 * @param documentID ID del documento de la finca en Firestore
 * @param imageUris Lista de URIs de las imágenes del anuncio
 * @param context Contexto de la aplicación
 * @param navController Controlador de navegación
 */
private fun borrarAnuncioFinca(
    userID: String,
    documentID: String,
    imageUris: List<Uri>,
    context: Context,
    navController: NavController
) {
    val refAlmacenamiento = FirebaseStorage.getInstance().reference
    val db = FirebaseFirestore.getInstance()

    if (imageUris.isNotEmpty()) {
        // Borrar las fotos del almacenamiento de Firebase Storage
        val tareasBorradoFotosStorage = imageUris.map { uri ->
            val nombreFoto = uri.lastPathSegment?.substringAfterLast('/')
            val refFoto = refAlmacenamiento.child("$userID/$documentID/$nombreFoto")
            // Estos pasos de extracción de la referencia están detallados con ejemplos en PantallaVivienda.kt
            refFoto.delete().addOnFailureListener { e ->
                val mensajeError = if (e.message?.contains("Object does not exist") == true) {
                    "La foto no existe en la ubicación especificada"
                } else {
                    "Error al borrar la foto: ${e.message}"
                }
                Log.e("PantallaFinca", mensajeError)
                Toast.makeText(context, mensajeError, Toast.LENGTH_SHORT).show()
            }
        }

        // Las fotos ya están borradas, pasa a borrar los documentos asociados a las fotos en Firestore
        Tasks.whenAll(tareasBorradoFotosStorage)
            .addOnSuccessListener {
                db.collection("fincas").document(documentID).collection("fotos")
                    .get()
                    .addOnSuccessListener { resultadoConsulta ->
                        val tareasBorradoDocumentosFirebase =
                            resultadoConsulta.documents.map { it.reference.delete() }
                        Tasks.whenAll(tareasBorradoDocumentosFirebase)
                            .addOnSuccessListener {
                                // Borrado del documento principal del anuncio
                                db.collection("fincas").document(documentID)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Anuncio borrado correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack() // Borro la pantalla actual de la pila de navegación
                                        navController.navigate("inicio")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(
                                            "PantallaFinca",
                                            "Error al borrar el anuncio: ${e.message}"
                                        )
                                        Toast.makeText(
                                            context,
                                            "Error al borrar el anuncio: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                    }
            }
            .addOnFailureListener { e ->
                Log.e("PantallaFinca", "Error al borrar las fotos: ${e.message}")
                Toast.makeText(
                    context,
                    "Error al borrar las fotos: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    } else {
        //Como la lista de fotos está vacía, se borra directamente el documento principal del anuncio
        db.collection("fincas").document(documentID)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Anuncio borrado correctamente", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Borro la pantalla actual de la pila de navegación
                navController.navigate("inicio")
            }
            .addOnFailureListener { e ->
                Log.e("PantallaFinca", "Error al borrar el anuncio: ${e.message}")
                Toast.makeText(
                    context,
                    "Error al borrar el anuncio: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}

/**
 * Carga una  finca desde Firestore
 * @param id ID de la finca
 * @return Finca
 */
suspend fun cargarFincaPorID(id: String): Finca? {
    return try {
        FirebaseFirestore.getInstance()
            .collection("fincas")
            .document(id)
            .get()
            .await()
            .toObject(Finca::class.java) // Convierte el documento en un objeto de la clase Finca
    } catch (e: Exception) {
        Log.e("PantallaFinca", "Error al obtener la finca: ${e.message}")
        null
    }
}

/**
 * Cambia el estado de una finca a no disponible
 * @param documentID ID de la finca
 */
suspend fun fincaTransada(documentID: String) {
    val db = FirebaseFirestore.getInstance()
    val fincaRef = db.collection("fincas").document(documentID)
    db.runTransaction { transaction ->
        transaction.update(fincaRef, "disponible", false)
    }.await()
}
