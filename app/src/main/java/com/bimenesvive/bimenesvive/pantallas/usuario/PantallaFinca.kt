package com.bimenesvive.bimenesvive.pantallas.usuario

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.bimenesvive.bimenesvive.modelo.Finca
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun PantallaFinca(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    documentID: String
) {
    val finca = remember { mutableStateOf<Finca?>(null) }
    val fotos = remember { mutableStateOf<List<Uri>>(emptyList()) }


    LaunchedEffect(documentID) {
        finca.value = getMiFincaById(documentID)
        fotos.value = getFotosFincaFromFirestore(documentID)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Pantalla Finca con ID: $documentID")
        Spacer(modifier = Modifier.height(16.dp))

        finca.value?.let {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = it.titulo, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = it.descripcion, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = it.parroquia, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "${it.metros} metros cuadrados", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = if (it.edificable) "Edificable" else "No Edificable", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = it.telefono, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = it.email, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Carrusel de fotos
            LazyRow {
                items(fotos.value) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(128.dp)
                            .padding(4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


