package com.bimenesvive.bimenesvive.pantallas.usuario

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bimenesvive.bimenesvive.autenticacion.EstadoAutenticacion
import com.bimenesvive.bimenesvive.autenticacion.AutenticacionViewModel
import com.bimenesvive.bimenesvive.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun PantallaPrincipal(
    modifier: Modifier = Modifier,
    navController: NavController,
    autenticacionViewModel: AutenticacionViewModel
) {

    val authenticationState = autenticacionViewModel.estadoAutenticacion.observeAsState()
    val userID = FirebaseAuth.getInstance().currentUser?.uid
    var isAdmin by remember { mutableStateOf(false) }

    /**
     * Comprueba si el usuario actual es el administrador al iniciar la pantalla
     */
    LaunchedEffect(userID) {
        comprobarAdmin { result ->
            isAdmin = result
        }
    }

    /**
     * Navega a la pantalla de login si el usuario no está autenticado
     */
    LaunchedEffect(authenticationState.value) {
        when (authenticationState.value) {
            is EstadoAutenticacion.Desautenticado -> {
                while (navController.popBackStack()) {
                    // Sigue vaciando la pila hasta que devuelve false
                }
                navController.navigate("login")
            }

            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Título
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botones centrados
        Button(
            onClick = { navController.navigate("explorar") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Home, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Ver Inmuebles")
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = { navController.navigate("subirPropiedad") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Subir Propiedad")
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = { navController.navigate("mispropiedades") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Assignment, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Mis Propiedades")
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = { navController.navigate("bimenes") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Park, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Conoce Bimenes")
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = { navController.navigate("anuncio") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AutoStories, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Tablón de Anuncios")
            }
        }

// Botón de inmuebles transados solo para el administrador
        if (isAdmin) {
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = { navController.navigate("transados") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoStories, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Inmuebles Transados")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Textbutton LOGOFF pegado al borde inferior
        TextButton(
            onClick = {
                autenticacionViewModel.cerrarSesion()
                // navController.navigate("login")
                // cedo la responsabilidad de navegar al LauncedEffect que provocará el cierre de sesión
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Cerrar sesión")
        }
    }
}

/**
 * Comprueba si el usuario actual es el administrador
 * @param onResult Función que se ejecuta cuando se obtiene el resultado
 * @return Boolean indica si el usuario actual es el administrador
 */
fun comprobarAdmin(onResult: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val adminID = db.collection("config").document("L2OLHKzjbA9wzA5N9D5W")
    val userID = FirebaseAuth.getInstance().currentUser?.uid

    adminID.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val adminID = document.getString("adminID")
                onResult(userID == adminID)
            } else {
                onResult(false)
            }
        }

}