package com.bimenesvive.bimenesvive.pantallas.usuario

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bimenesvive.bimenesvive.AutenticacionViewModel
import com.bimenesvive.bimenesvive.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import com.bimenesvive.bimenesvive.modelo.Finca
import com.bimenesvive.bimenesvive.modelo.Vivienda
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.bimenesvive.bimenesvive.utilidades.*
import androidx.compose.ui.graphics.Brush
import com.google.firebase.auth.FirebaseAuth


// UI de la pantalla de explorar
@Composable
fun PantallaExplorar(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    var propiedades by remember { mutableStateOf<List<Any>>(emptyList()) } // Lista de propiedades de Card, si quiero que sean distintas es necesario
    var inmuebleSeleccionado by remember { mutableStateOf("viviendas") } // Controla la selección del navbar inferior, viviendas está seleccionado por defecto
    val scope =
        rememberCoroutineScope() // Las corrutinas son hilos de ejecución que se pueden pausar y reanudar


    // LaunchedEffect se ejecuta cuando se compone por primera vez y cuando selectedTab cambia
    LaunchedEffect(inmuebleSeleccionado) {
        scope.launch {
            propiedades =
                if (inmuebleSeleccionado == "viviendas") getViviendas() else getFincas() // Llamada a la base de datos
        }
    }

    /**
     * Función que maneja el evento de selección de inmueble en la navbar inferior
     */
    fun onInmuebleSeleccionado(seleccion: String) { // Maneja el evento de selección del inmueble en la navbar inferior
        inmuebleSeleccionado = seleccion // este cambio provoca que se ejecute LaunchedEffect
    }

    Scaffold(
        //BOTTOM BAR
        bottomBar = {
            NavBarInferior(
                navController,
                inmuebleSeleccionado,
                ::onInmuebleSeleccionado // Paso una función como parámetro
            )
        }
    ) {

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it) // El padding obtenido del Scaffold, asegura que el contenido
                                // no se superponga con la barra inferior
        ) {
            items(propiedades) { propiedad ->
                when (propiedad) {
                    is Vivienda -> ViviendaItem(propiedad, navController)
                    is Finca -> FincaItem(propiedad, navController)
                }
            }
        }
    }
}


// Tarjeta vivienda
@Composable
fun ViviendaItem(vivienda: Vivienda, navController: NavHostController) {
    val fotos = remember { mutableStateOf<List<Uri>>(emptyList()) } // Lista de fotos de la vivienda
    val coroutineScope = rememberCoroutineScope() // Utilizo esta corrutina para navegar a otra pantalla

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

// Tarjeta finca
@Composable
fun FincaItem(finca: Finca, navController: NavHostController) {
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

@Composable
fun NavBarInferior(
    navController: NavHostController,
    seccionSeleccionada: String,
    alSeleccionarSeccion: (String) -> Unit

    // seccionSeleccionada es el estado que controla qué sección está seleccionada
    // lo utilizo para cambiar el color de fondo (una sombra) que indica si estamos en
    // la sección de viviendas o de fincas

) {

    val userID = FirebaseAuth.getInstance().currentUser?.uid ?: "invitado"

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
    ) {
        NavigationBarItem(
            // el parámetro icon es una función lambda que devuelve un Icon
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_atras),
                    contentDescription = "Menú"
                )
            },
            label = { Text("Menú") },
            selected = seccionSeleccionada == "menu",
            onClick = {
                if (userID == "invitado") {
                    navController.navigate("invitado")
                } else {
                    navController.navigate("inicio")
                }
            }

        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_casa),
                    contentDescription = "Viviendas"
                )
            },
            label = { Text("Viviendas") },
            selected = seccionSeleccionada == "viviendas",
            onClick = { alSeleccionarSeccion("viviendas") }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_finca),
                    contentDescription = "Fincas"
                )
            },
            label = { Text("Fincas") },
            selected = seccionSeleccionada == "fincas",
            onClick = { alSeleccionarSeccion("fincas") }
        )
    }
}

//Funciones relacionadas con Firestore, que sólo se usan en esta pantalla


/**
 * Obtiene las viviendas desde Firestore
 */
suspend fun getViviendas(): List<Vivienda> {
//Suspend indica que la función es una corrutina, puede ser pausada y reanudada
    val db = FirebaseFirestore.getInstance()
    return try {
        val resultadoConsulta = db.collection("viviendas").get().await()
        resultadoConsulta.toObjects(Vivienda::class.java)
            .filter { it.disponible } //Convierte los documentos obtenidos en una lista de objetos de la clase
    } catch (e: Exception) {
        emptyList()
    }
}


/**
 * Obtiene las fincas desde Firestore
 */
suspend fun getFincas(): List<Finca> {
    val db = FirebaseFirestore.getInstance()
    return try {
        val resultadoConsulta = db.collection("fincas").get().await()
        resultadoConsulta.toObjects(Finca::class.java)
            .filter { it.disponible }
    } catch (e: Exception) {
        emptyList()
    }
}

/**
 * Sube una visita a la vivienda
 */
suspend fun subirVisitaVivienda(documentID: String) {
    val db = FirebaseFirestore.getInstance()
    val viviendaRef = db.collection("viviendas").document(documentID)
    db.runTransaction { transaction ->
        val snapshot = transaction.get(viviendaRef)
        val nuevasVisitas = snapshot.getLong("visitas")?.plus(1) ?: 1
        transaction.update(viviendaRef, "visitas", nuevasVisitas)
    }.await()
}

/**
 * Sube una visita a la finca
 */
suspend fun subirVisitaFinca(documentID: String) {
    val db = FirebaseFirestore.getInstance()
    val fincaRef = db.collection("fincas").document(documentID)
    db.runTransaction { transaction ->
        val snapshot = transaction.get(fincaRef)
        val nuevasVisitas = snapshot.getLong("visitas")?.plus(1) ?: 1
        transaction.update(fincaRef, "visitas", nuevasVisitas)
    }.await()
}







//Flujo del código al seleccionar en la navbar un elemento:
//1 Ejecuta la función onInmuebleSeleccionado
//2 Cambia el valor de inmuebleSeleccionado
//3 Ejecuta LaunchedEffect, que a su vez ejecuta la corrutina
//4 La corrutina dentro de LaunchedEffect realiza una consulta a Firestore para obtener las propiedades inmobiliarias
//5 Actualiza la lista de propiedades de la Card con los resultados de la consulta
//6 Se vuelve a pintar la UI con las nuevas propiedades



