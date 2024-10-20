package com.bimenesvive.bimenesvive.pantallas.usuario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.bimenesvive.bimenesvive.modelo.Anuncio
import com.bimenesvive.bimenesvive.utilidades.NavBarVolverInicio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.graphics.Color

@Composable
fun PantallaAnuncio(modifier: Modifier = Modifier, navController: NavHostController) {
    val userID = FirebaseAuth.getInstance().currentUser?.uid ?: "invitado"
    var anuncios by remember { mutableStateOf(listOf<Anuncio>()) }
    var showDialog by remember { mutableStateOf(false) }
    var userAnuncio by remember { mutableStateOf<Anuncio?>(null) }

    val backgroundColors = listOf(
        Color(0xFFC8E6C9), // Light Green
        Color(0xFFBBDEFB), // Light Blue
        Color(0xFFFFE0B2)  // Light Orange
    )

    LaunchedEffect(Unit) {
        anuncios = importarAnunciosFireBase()
        userAnuncio = anuncios.find { it.userID == userID } //almacena el anuncio del usuario actual
    }

    Scaffold(
        bottomBar = { NavBarVolverInicio(navController) },
        floatingActionButton = {
            if (userID != "invitado") {
                FloatingActionButton(onClick = {
                    if (userAnuncio != null) {
                        borrarAnuncio(userAnuncio!!) // !! = // Lanza NullPointerException si userAnuncio es null
                        userAnuncio = null
                        anuncios =
                            anuncios.filter { it.userID != userID } // Actualizo la lista de anuncios, filtro los del usuario para asegurar
                        // que no aparezca el anuncio que acabo de borrar, en caso de que Firebase tardase en actualizar
                    } else {
                        showDialog = true
                    }
                }) {
                    Icon(
                        imageVector = if (userAnuncio != null) Icons.Default.Delete else Icons.Default.Add,
                        contentDescription = if (userAnuncio != null) "Borrar Anuncio" else "Añadir Anuncio"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                "Tablón de Anuncios",
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(anuncios) { index, anuncio ->
                    val colorIndex =
                        index % backgroundColors.size // Cambia el color de fondo de cada anuncio
                    CardAnuncio(
                        anuncio,
                        backgroundColors[colorIndex]
                    ) // Paso el color de fondo por parámetro
                }
            }
        }
    }

    if (showDialog) {
        AnuncioDialog(onDismiss = { showDialog = false }) { newAnuncio ->
            val nuevoAnuncio =
                newAnuncio.copy(userID = userID) //nuevo anuncio con el userID del usuario actual
            addAnuncio(nuevoAnuncio)
            anuncios = anuncios + nuevoAnuncio
            userAnuncio = nuevoAnuncio
            showDialog = false // Cierro el diálogo
        }
    }
}

/**
 * Importa los anuncios de Firebase
 */
suspend fun importarAnunciosFireBase(): List<Anuncio> {
    return try {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("anuncios")
            .get()
            .await()
        val anuncios =
            snapshot.documents.mapNotNull { it.toObject(Anuncio::class.java) } // Convierte los documentos en objetos de la clase Anuncio
        return anuncios    // Devuelve la lista de anuncios (puedo omitir la palabra return)
    } catch (e: Exception) {
        emptyList()
    }
}

/**
 * Añade un anuncio a Firebase
 */
fun addAnuncio(anuncio: Anuncio) {
    FirebaseFirestore.getInstance()
        .collection("anuncios")
        .add(anuncio)
}

/**
 * Borra un anuncio de Firebase
 */
fun borrarAnuncio(anuncio: Anuncio) {
    FirebaseFirestore.getInstance()
        .collection("anuncios")
        .whereEqualTo("userID", anuncio.userID)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                document.reference.delete()
            }
        }
}

/**
 * Composable que muestra un anuncio
 */
@Composable
fun CardAnuncio(anuncio: Anuncio, backgroundColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = anuncio.titulo,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(anuncio.mensaje)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Datos de contacto:")
            Text(anuncio.contacto)
        }
    }
}

/**
 * Diálogo para añadir un anuncio
 */
@Composable
fun AnuncioDialog(onDismiss: () -> Unit, onSubmit: (Anuncio) -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var contacto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Mi Anuncio")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = contacto,
                    onValueChange = { contacto = it },
                    label = { Text("Datos de contacto") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    label = { Text("Mensaje") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onSubmit(Anuncio(titulo, contacto, mensaje, userID = ""))
                    }) {
                        Text("Anunciar")
                    }
                }
            }
        }
    }
}