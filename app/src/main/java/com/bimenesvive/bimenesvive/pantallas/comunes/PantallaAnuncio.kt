package com.bimenesvive.bimenesvive.pantallas.comunes

import android.util.Log
import android.widget.Toast
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
import com.bimenesvive.bimenesvive.modelos.Anuncio
import com.bimenesvive.bimenesvive.componentes.NavBarVolverInicio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.bimenesvive.bimenesvive.ui.theme.AzulClaro
import com.bimenesvive.bimenesvive.ui.theme.NaranjaClaro
import com.bimenesvive.bimenesvive.ui.theme.VerdeClaro

@Composable
fun PantallaAnuncio(modifier: Modifier = Modifier, navController: NavHostController) {
    val userID = FirebaseAuth.getInstance().currentUser?.uid ?: "invitado"
    val context = LocalContext.current
    var anuncios by remember { mutableStateOf(listOf<Anuncio>()) } // Lista de anuncios cargados desde Firestore
    var showDialog by remember { mutableStateOf(false) } // Dialog para añadir anuncio
    var userAnuncio by remember { mutableStateOf<Anuncio?>(null) } // Anuncio del usuario actual
    var huboCambios by remember { mutableStateOf(false) }

    val coloresFondoTarjetas = listOf(
        VerdeClaro,
        AzulClaro,
        NaranjaClaro
    )

    LaunchedEffect(huboCambios) {
        anuncios = importarAnunciosFireBase()
        userAnuncio = anuncios.find { it.userID == userID }
    }

    Scaffold(
        bottomBar = { NavBarVolverInicio(navController) },
        floatingActionButton = {
            if (userID != "invitado") { // Los usuarios invitados no pueden añadir anuncios
                FloatingActionButton(onClick = {
                    userAnuncio?.let { anuncio ->
                        borrarAnuncioFirebase(anuncio, {
                            userAnuncio = null
                            huboCambios = !huboCambios // Trigger para hacer saltar el LaunchedEffect
                        }, { e ->
                            Log.e("PantallaAnuncio", "Error al borrar anuncio", e)
                            Toast.makeText(context, "Error al borrar el anuncio", Toast.LENGTH_SHORT).show()
                        })
                    } ?: run { // Si no hay anuncio del usuario, abro el Dialog para añadirlo
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
        floatingActionButtonPosition = FabPosition.End // End es un ENUM que coloca el action button al final
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
                // itemsIndexed es un composable que permite recorrer una lista con índice
                // y crear un composable por cada elemento
                itemsIndexed(anuncios) { index, anuncio ->
                    val colorIndex =
                        index % coloresFondoTarjetas.size // Cambia el color de fondo de cada anuncio, de forma ordenada y cíclica (0, 1, 2, 0, 1, 2...)
                    CardAnuncio(
                        anuncio,
                        coloresFondoTarjetas[colorIndex] // Paso el color de fondo por parámetro
                    )
                }
            }
        }
    }

    // Dialog con el formulario para añadir un anuncio
//    if (showDialog) {
//        AnuncioDialog(onDismiss = { showDialog = false }) { nuevoAnuncio ->
//            val anuncioActualizado = nuevoAnuncio.copy(userID = userID)
//            // Hago una copia de nuevoAnuncio y le añado el userID como nuevo campo
//            addAnuncio(anuncioActualizado)
//            anuncios = anuncios + anuncioActualizado
//            userAnuncio = anuncioActualizado
//            showDialog = false // Cierro el diálogo (dispara el LaunchedEffect)
//        }
//    }
    if (showDialog) {
        AnuncioDialog(onDismiss = { showDialog = false }) { nuevoAnuncio ->
            val anuncioActualizado = nuevoAnuncio.copy(userID = userID)
            addAnuncio(anuncioActualizado, {
                huboCambios = !huboCambios // Trigger para hacer saltar el LaunchedEffect
                showDialog = false
            }, { e ->
                Log.e("PantallaAnuncio", "Error al añadir anuncio", e)
                Toast.makeText(context, "Error al añadir el anuncio", Toast.LENGTH_SHORT).show()
            })
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
 * Dialog para añadir un anuncio
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
                        // El userID se modifica a posteriori en   if (showDialog) {
                    }) {
                        Text("Anunciar")
                    }
                }
            }
        }
    }
}

// Métodos para interactuar con Firebase (exclusivos de esta pantalla)

/**
 * Importa los anuncios de Firebase
 * @return Lista de anuncios
 */
suspend fun importarAnunciosFireBase(): List<Anuncio> {
    return try {
        val resultadoConsulta = FirebaseFirestore.getInstance()
            .collection("anuncios")
            .get()
            .await()
        val anuncios =
            resultadoConsulta.documents.mapNotNull { it.toObject(Anuncio::class.java) } // Convierte los documentos en objetos de la clase Anuncio
        return anuncios    // Devuelve la lista de anuncios (puedo omitir la palabra return)
    } catch (e: Exception) {
        emptyList()
    }
}

/**
 * Añade un anuncio a Firebase
 * @param anuncio: Anuncio a añadir
 */
//fun addAnuncio(anuncio: Anuncio) {
//    FirebaseFirestore.getInstance()
//        .collection("anuncios")
//        .add(anuncio)
//}
fun addAnuncio(anuncio: Anuncio, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
    FirebaseFirestore.getInstance()
        .collection("anuncios")
        .add(anuncio)
        .addOnSuccessListener { onComplete() }
        .addOnFailureListener { e -> onFailure(e) }
}

/**
 * Borra un anuncio de Firebase
 * @param anuncio: Anuncio a borrar
 */
fun borrarAnuncioFirebase(anuncio: Anuncio, onComplete: () -> Unit, onFailure: (Exception) -> Unit) {
    FirebaseFirestore.getInstance()
        .collection("anuncios")
        .whereEqualTo("userID", anuncio.userID)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                document.reference.delete()
                    .addOnSuccessListener { onComplete() }
                    .addOnFailureListener { e -> onFailure(e) }
            }
        }
        .addOnFailureListener { e -> onFailure(e) }
}


// Flujo del código cuando se está añadiendo un anuncio:
// 1 El usuario rellena el formulario del Dialog y pulsa Anunciar
// 2 Se ejecuta la lambda onSubmit de AnuncioDialog
// 3 Se añade el anuncio a Firebase con addAnuncio
// 4 Se añade el anuncio a la lista anuncios local
// 5 Se actualiza el anuncio del usuario actual (userAnuncio)
// 6 Se cierra el Dialog con showDialog = false





