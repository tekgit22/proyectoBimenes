package com.bimenesvive.bimenesvive.componentes

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bimenesvive.bimenesvive.modelos.Finca
import com.bimenesvive.bimenesvive.modelos.Vivienda
import com.bimenesvive.bimenesvive.pantallas.comunes.subirVisitaFinca
import com.bimenesvive.bimenesvive.pantallas.comunes.subirVisitaVivienda
import com.bimenesvive.bimenesvive.utilidades.getFotosFincaDesdeFirestore
import com.bimenesvive.bimenesvive.utilidades.getFotosViviendaDesdeFirestore
import kotlinx.coroutines.launch



@Composable
fun TarjetaVivienda(vivienda: Vivienda, navController: NavHostController) {
    val fotos = remember { mutableStateOf<List<Uri>>(emptyList()) } // Lista de fotos de la vivienda
    val coroutineScope =
        rememberCoroutineScope() // Utilizo esta corrutina para navegar a otra pantalla

    // LaunchedEffect se ejecuta cuando se compone por primera vez y cuando cambia el documentID de la vivienda
    LaunchedEffect(vivienda.documentID) {
        fotos.value = getFotosViviendaDesdeFirestore(vivienda.documentID)
    }

    //val alturaTarjeta = if (fotos.value.isEmpty()) 200.dp else 210.dp

    val gradiente = Brush.linearGradient(
        colors = listOf(
            Color.White,
            Color(0xFFBBDEFB)  // Azul claro
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            //.height(alturaTarjeta)
            .clickable {
                coroutineScope.launch {
                    subirVisitaVivienda(vivienda.documentID)
                    navController.navigate("vivienda/${vivienda.documentID}")
                }
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradiente)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Título
                Text(text = vivienda.titulo, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                // Foto y datos
                Row {
                    ImagenConPlaceholder(
                        uri = fotos.value.firstOrNull()
                            ?: Uri.parse("android.resource://com.bimenesvive.bimenesvive/drawable/nofoto"),
                        modifier = Modifier
                            .size(150.dp)
                            .padding(end = 16.dp)
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = vivienda.ubicacion,
                            Modifier.padding(top = 16.dp)
                        )
                        Text(text = "${vivienda.habitaciones} ${if (vivienda.habitaciones == 1) "habitación" else "habitaciones"}")
                        Text(text = "${vivienda.metros} m²")
                        Text(
                            text = if (vivienda.precio == 0) "Precio no disponible" else "Precio: ${vivienda.precio} €",
                            modifier = Modifier
                                .wrapContentWidth()
                                .wrapContentHeight()
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TarjetaFinca(finca: Finca, navController: NavHostController) {
    val fotos = remember { mutableStateOf<List<Uri>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(finca.documentID) {
        fotos.value = getFotosFincaDesdeFirestore(finca.documentID)
    }

    //  val alturaTarjeta = if (fotos.value.isEmpty()) 200.dp else 210.dp

    val gradiente = Brush.linearGradient(
        colors = listOf(
            Color.White, // Blanco
            Color(0xFFC8E6C9)  // Verde actual
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            //    .height(alturaTarjeta)
            .clickable {
                coroutineScope.launch { // SubirVisitaFinca es una corrutina y tenemos que esperar a que termine
                    subirVisitaFinca(finca.documentID)
                    navController.navigate("finca/${finca.documentID}")
                }
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradiente)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Título
                Text(text = finca.titulo, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                // Foto y datos
                Row {
                    ImagenConPlaceholder(
                        uri = fotos.value.firstOrNull()
                            ?: Uri.parse("android.resource://com.bimenesvive.bimenesvive/drawable/nofoto"),
                        modifier = Modifier
                            .size(150.dp)
                            .padding(end = 16.dp)
                    )
                    //Columna que muestra los datos de la finca a la derecha de la foto
                    Column {
                        Text(
                            text = finca.ubicacion, Modifier.padding(top = 16.dp)
                        )
                        Text(text = "${finca.metros} m²")
                        Text(text = if (finca.edificable) "Edificable" else "No edificable")
                        Text(
                            text = if (finca.precio == 0) "Precio no disponible" else "Precio: ${finca.precio} €",
                            modifier = Modifier
                                .wrapContentWidth()
                                .wrapContentHeight()
                        )
                    }
                }
            }
        }
    }
}
