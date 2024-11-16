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
import com.bimenesvive.bimenesvive.modelos.Vivienda
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.bimenesvive.bimenesvive.componentes.NavBarViendoPropiedad
import com.bimenesvive.bimenesvive.modelos.EstadoPropiedad
import com.google.firebase.storage.StorageReference


@Composable
fun PantallaVivienda(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    documentID: String
) {
    var vivienda by remember { mutableStateOf<Vivienda?>(null) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var esImagenGrande by remember { mutableStateOf(false) }
    var imagenSeleccionada by remember { mutableStateOf<Uri?>(null) }
    val userID = FirebaseAuth.getInstance().currentUser?.uid ?: "invitado"
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current as Activity
    var ownerUserID by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState() // Para controlar el scroll de las imágenes
    val scrollState = rememberScrollState()

    // Solicitar permisos en tiempo de ejecución a usuarios con SO Android 13 +
    requestPhotoPermissions(context)

    LaunchedEffect(documentID) {
        vivienda = getViviendaById(documentID)
        imageUris = getFotosViviendaDesdeFirestore(documentID)
    }

    LaunchedEffect(documentID) {
        try {
            vivienda = getViviendaById(documentID)
            imageUris = getFotosViviendaDesdeFirestore(documentID)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                    Text(text = vivienda?.descripcion ?: "")
                    AssistChip(
                        onClick = {},
                        label = { Text(vivienda?.ubicacion ?: "") },
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
                        vivienda?.let {
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
                            label = { Text("${vivienda?.metros} m²") },
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
                            label = { Text("Habitaciones: ${vivienda?.habitaciones}") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Bed,
                                    contentDescription = null,
                                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                                )
                            },
                        )
                    }
                    Column {
                        if (vivienda?.precio == 0) {
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
                                        Text(text = "Precio: ${vivienda?.precio} ")
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // LazyRow para mostrar las imágenes
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

                    // Row encargada de mostrar los botones de navegación entre las imágenes
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
                                            listState.animateScrollToItem(listState.firstVisibleItemIndex - 1)
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
                                            listState.animateScrollToItem(listState.firstVisibleItemIndex + 1)
                                        }
                                    },
                                tint = Color.Gray
                            )
                        } else {
                            //Spacer(modifier = Modifier.size(24.dp))
                        }
                    }

                    Spacer(modifier = Modifier.size(24.dp))

                    //Primera row de botones

                    if (userID == vivienda?.userID) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { launcher.launch("image/*") },
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
                                        Log.d(
                                            "PantallaVivienda",
                                            "Permiso de ubicación no concedido"
                                        )
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

                    //Columna que contiene el botón de ver localización o localización no disponible
                    Column {
                        if (vivienda?.localizacion?.latitude == 0.0 && vivienda?.localizacion?.longitude == 0.0) {
                            Button(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                            {
                                Text("Localización no disponible")
                            }
                        } else {
                            Button(
                                onClick = {
                                    vivienda?.localizacion?.let { geoPoint ->
                                        val googleMapsIntentUri =
                                            Uri.parse("geo:${geoPoint.latitude},${geoPoint.longitude}?q=${geoPoint.latitude},${geoPoint.longitude}(${vivienda?.titulo})")
                                        val mapIntent =
                                            Intent(Intent.ACTION_VIEW, googleMapsIntentUri).apply {
                                                setPackage("com.google.android.apps.maps")
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                        context.startActivity(mapIntent)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place, // Use the map icon
                                    contentDescription = "Icono de mapa"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ver Localización")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (userID == vivienda?.userID) {
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
                                            viviendaTransada(documentID)
                                            Toast.makeText(
                                                context,
                                                "El anuncio se ha archivado",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.navigate("inicio") {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    inclusive = true
                                                }
                                            }
                                        } catch (e: Exception) {
                                            Log.e(
                                                "PantallaVivienda",
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
                                    containerColor = Color(
                                        0xFF4CAF50
                                    )
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Euro, // Replace with your desired icon
                                    contentDescription = "Símbolo €"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Transado")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    try {
                                        borrarAnuncioVivienda(
                                            userID,
                                            documentID,
                                            imageUris,
                                            context,
                                            navController
                                        )
                                    } catch (e: Exception) {
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
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFF44336
                                    )
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
                } //final de la columna scrollable

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
 * Borra un anuncio de vivienda de Firestore y sus fotos del almacenamiento
 * @param userID ID del usuario
 * @param documentID ID del anuncio
 * @param imageUris Lista de URIs de las fotos
 * @param context Contexto de la aplicación
 * @param navController Controlador de navegación
 */
private fun borrarAnuncioVivienda(
    userID: String,
    documentID: String,
    imageUris: List<Uri>,
    context: Context,
    navController: NavHostController
) {
    val refAlmacenamiento = FirebaseStorage.getInstance().reference
    val db = FirebaseFirestore.getInstance()

    if (imageUris.isNotEmpty()) {
        // Borrado de todas las fotos en Firebase Storage
        val tareasBorradoFotosStorage = imageUris.map { uri ->
            val nombreFoto = uri.lastPathSegment?.substringAfterLast('/')
            val refFoto: StorageReference =
                refAlmacenamiento.child("$userID/$documentID/$nombreFoto")
//            Log.d("borrarAnuncioVivienda", "uri: $uri")
//            Log.d("borrarAnuncioVivienda", "nombreFoto: $nombreFoto")
//            Log.d("borrarAnuncioVivienda", "referenciaFoto: ${referenciaFoto.path}")

            /* Ejemplo:
            LOG:
                uri: https://firebasestorage.googleapis.com/v0/b/bimenes-vive.appspot.com/o/4wOzuh8c8GhYeP3qbgn6Ja888F02%2FRvwMSNZVnf4bmmR3TNMa%2F05cdb4a0-53c7-4af0-a10a-a478ee7cef25.jpg?alt=media&token=e7ab36ab-b999-4d54-a0ae-bca40b8355c0
                nombreFoto: 05cdb4a0-53c7-4af0-a10a-a478ee7cef25.jpg
                referenciaFoto: /4wOzuh8c8GhYeP3qbgn6Ja888F02/RvwMSNZVnf4bmmR3TNMa/05cdb4a0-53c7-4af0-a10a-a478ee7cef25.jpg

            EXPLICACIÓN:
                lastPathSegment selecciona: 4wOzuh8c8GhYeP3qbgn6Ja888F02/RvwMSNZVnf4bmmR3TNMa/05cdb4a0-53c7-4af0-a10a-a478ee7cef25.jpg
                lastPath Segment sólo procesa la ruta del archivo y omite los parámetros de la query (?alt=media&token=e7ab36ab-b999-4d54-a0ae-bca40b8355c0)
                substringAfterLast('/') selecciona: 05cdb4a0-53c7-4af0-a10a-a478ee7cef25.jpg

                Pese a que lo que devuelve lastPathSegment parece ser lo mismo que referenciaFoto con un / delante,
                referenciaFoto es del tipo StorageReference y lastPathSegment es un String, por eso
                en lugar de usar inferencia de tipos, declaro específicamente como StorageReference para que quede claro
            */


            refFoto.delete().addOnFailureListener { e ->
                val mensajeError = if (e.message?.contains("Object does not exist") == true) {
                    "La foto no existe en el almacenamiento"
                } else {
                    "Error al borrar la foto: ${e.message}"
                }
                Log.e("PantallaVivienda", mensajeError)
                Toast.makeText(context, mensajeError, Toast.LENGTH_SHORT).show()
            }
        }

        // Las fotos ya están borradas, pasa a borrar los documentos asociados a las fotos en Firestore
        Tasks.whenAll(tareasBorradoFotosStorage)
            .addOnSuccessListener {
                db.collection("viviendas").document(documentID).collection("fotos")
                    .get()
                    .addOnSuccessListener { resultadoConsulta ->
                        val tareasBorradoDocumentosFirebase =
                            resultadoConsulta.documents.map { it.reference.delete() }
                        Tasks.whenAll(tareasBorradoDocumentosFirebase)
                            .addOnSuccessListener {
                                // Borrado del documento principal del anuncio
                                db.collection("viviendas").document(documentID)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Anuncio borrado correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack()
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
        //Como la lista de fotos está vacía, se borra directamente el documento principal del anuncio
        db.collection("viviendas").document(documentID)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Anuncio borrado correctamente", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
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


/**
 * Carga una vivienda desde Firestore
 * @param id ID de la vivienda
 * @return Vivienda
 */
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

/**
 * Cambia disponible a false en una vivienda
 * @param documentID ID de la vivienda
 */
suspend fun viviendaTransada(documentID: String) {
    val db = FirebaseFirestore.getInstance()
    val viviendaRef = db.collection("viviendas").document(documentID)
    db.runTransaction { transaction ->
        transaction.update(viviendaRef, "disponible", false)
    }.await()
}

/**
 * Cambia el estado de una finca a no disponible
 */
suspend fun fincaTransada(documentID: String) {
    val db = FirebaseFirestore.getInstance()
    val fincaRef = db.collection("fincas").document(documentID)
    db.runTransaction { transaction ->
        transaction.update(fincaRef, "disponible", false)
    }.await()
}

