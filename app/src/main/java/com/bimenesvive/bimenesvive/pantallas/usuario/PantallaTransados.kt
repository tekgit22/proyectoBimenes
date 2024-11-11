package com.bimenesvive.bimenesvive.pantallas.usuario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bimenesvive.bimenesvive.componentes.TarjetaFinca
import com.bimenesvive.bimenesvive.componentes.TarjetaVivienda
import com.bimenesvive.bimenesvive.modelo.Finca
import com.bimenesvive.bimenesvive.modelo.Vivienda
import com.bimenesvive.bimenesvive.utilidades.NavBarVolverInicio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun PantallaTransados(modifier: Modifier = Modifier, navController: NavHostController) {
    var propiedades by remember { mutableStateOf<List<Any>>(emptyList()) }
    val userID = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(userID) {
        propiedades = cargarPropiedadesTransados()
    }

    Scaffold(
        bottomBar = { NavBarVolverInicio(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Propiedades vendidas o alquiladas",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(propiedades) { propiedad ->
                    when (propiedad) {
                        is Vivienda -> TarjetaVivienda(propiedad, navController)
                        is Finca -> TarjetaFinca(propiedad, navController)
                    }
                }
            }
        }
    }
}

/**
 * Carga las propiedades transadas del usuario actual.
 * @return Lista de propiedades transadas.
 */
suspend fun cargarPropiedadesTransados(): List<Any> {
    val viviendas = getViviendasTransadas()
    val fincas = getFincasTransadas()
    return viviendas + fincas
}

/**
 * Obtiene las viviendas transadas del usuario actual.
 * @return Lista de viviendas transadas.
 */
suspend fun getViviendasTransadas(): List<Vivienda> {
    val db = FirebaseFirestore.getInstance()
    return try {
        val resultadoConsulta = db.collection("viviendas")
            .whereEqualTo("disponible", false)
            .get()
            .await()
        resultadoConsulta.toObjects(Vivienda::class.java)
    } catch (e: Exception) {
        emptyList()
    }
}

/**
 * Obtiene las fincas transadas del usuario actual.
 * @return Lista de fincas transadas.
 */
suspend fun getFincasTransadas(): List<Finca> {
    val db = FirebaseFirestore.getInstance()
    return try {
        val resultadoConsulta = db.collection("fincas")
            .whereEqualTo("disponible", false)
            .get()
            .await()
        resultadoConsulta.toObjects(Finca::class.java)
    } catch (e: Exception) {
        emptyList()
    }
}