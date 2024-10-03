package com.bimenesvive.bimenesvive.pantallas.usuario

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bimenesvive.bimenesvive.modelo.Vivienda
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

@Composable
fun PantallaMiVivienda(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    documentID: String
) {
    var vivienda by remember { mutableStateOf<Vivienda?>(null) }

    LaunchedEffect(documentID) {
        vivienda = getViviendaById(documentID)
        Log.d("PantallaVivienda", "Vivienda: $vivienda")
    }

    vivienda?.let {
        Column(modifier = modifier.padding(16.dp)) {
            Text(text = "MI VIVIENDA, aquí podrá añadir fotos o borrar la vivienda")
            Spacer(modifier = Modifier.height(8.dp))
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
            Text(text = "FOTOGRAFÍAS") //buscar la forma más bonita de mostrar las fotos, miniaturas, carrusel...
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = "MAPA GPS") //Valorar si hacerlo un botón que lleve a otra pantalla o mostrar minimapa
        }
    } ?: run {
        Log.d("PantallaVivienda", "No se encontró la vivienda con id: $documentID")
    }
}

suspend fun getMiViviendaById(id: String): Vivienda? {
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