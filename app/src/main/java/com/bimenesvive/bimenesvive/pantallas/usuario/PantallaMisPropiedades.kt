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
fun PantallaMisPropiedades(modifier: Modifier = Modifier, navController: NavHostController) {
    var propiedades by remember { mutableStateOf<List<Any>>(emptyList()) }
    val userID = FirebaseAuth.getInstance().currentUser?.uid

    // Cargar propiedades al iniciar la pantalla
    LaunchedEffect(userID) {
        userID?.let {
            propiedades = cargarPropiedades(it)
        }
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
                text = "Mis Propiedades",
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
 * Carga las propiedades del usuario desde Firestore
 * @param userID ID del usuario
 * @return Lista de propiedades del usuario
 */
suspend fun cargarPropiedades(userID: String): List<Any> {
    val viviendas = getMisViviendas(userID)
    val fincas = getMisFincas(userID)
    return viviendas + fincas
}

/**
 * Carga las viviendas del usuario desde Firestore
 * @param userID ID del usuario
 * @return Lista de viviendas del usuario
 */
suspend fun getMisViviendas(userID: String): List<Vivienda> {
    val db = FirebaseFirestore.getInstance()
    return try {
        val resultadoConsulta = db.collection("viviendas")
            .whereEqualTo("userID", userID)
            .get()
            .await()
        resultadoConsulta.toObjects(Vivienda::class.java).filter { it.disponible } // sólo almaceno las viviendas que están disponibles
    } catch (e: Exception) {
        emptyList()
    }
}

/**
 * Carga las fincas del usuario desde Firestore
 * @param userID ID del usuario
 * @return Lista de fincas del usuario
 */
suspend fun getMisFincas(userID: String): List<Finca> {
    val db = FirebaseFirestore.getInstance()
    return try {
        val resultadoConsulta = db.collection("fincas")
            .whereEqualTo("userID", userID)
            .get()
            .await()
        resultadoConsulta.toObjects(Finca::class.java).filter { it.disponible } // sólo almaceno las fincas que están disponibles
    } catch (e: Exception) {
        emptyList()
    }
}