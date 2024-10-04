package com.bimenesvive.bimenesvive.pantallas.usuario

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.bimenesvive.bimenesvive.modelo.Vivienda
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.clickable

@Composable
fun PantallaVivienda(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    documentID: String
) {
    var vivienda by remember { mutableStateOf<Vivienda?>(null) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

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

