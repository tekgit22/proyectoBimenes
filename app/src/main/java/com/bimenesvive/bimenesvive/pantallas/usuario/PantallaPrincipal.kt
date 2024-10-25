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
import com.bimenesvive.bimenesvive.AuthenticationState
import com.bimenesvive.bimenesvive.AutenticacionViewModel
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
import androidx.compose.ui.platform.LocalContext


@Composable
fun PantallaPrincipal(
    modifier: Modifier = Modifier,
    navController: NavController,
    autenticacionViewModel: AutenticacionViewModel
) {
    val authenticationState = autenticacionViewModel.estadoAutenticacion.observeAsState()
    val userID = FirebaseAuth.getInstance().currentUser?.uid
//    val adminID = "4wOzuh8c8GhYeP3qbgn6Ja888F02"
    var isAdmin by remember { mutableStateOf(false) }
    val context = LocalContext.current

    /**
     * Comprueba si el usuario actual es el administrador
     */
    LaunchedEffect(userID) {
       // Toast.makeText(context, "userID=($userID)", Toast.LENGTH_SHORT).show()
        comprobarAdmin { result ->
            isAdmin = result
        }
    }

    // REVISAR SU UTILIDAD
    LaunchedEffect(authenticationState.value) {
        when (authenticationState.value) {
            is AuthenticationState.Desautenticado -> navController.navigate("login")
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
            painter = painterResource(id = R.drawable.logotest),
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

//
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
        //Este spacer se encarga de que los botones anteriores no se peguen al borde inferior de la pantalla


        // Botón de logoff en la parte inferior
        TextButton(
            onClick = {
                autenticacionViewModel.cerrarSesion()
                navController.navigate("login")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Cerrar sesión")
        }
    }
}

fun comprobarAdmin(onResult: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val adminRef = db.collection("config").document("L2OLHKzjbA9wzA5N9D5W")
    val userID = FirebaseAuth.getInstance().currentUser?.uid

    adminRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val adminID = document.getString("adminID")
                onResult(userID == adminID)
            } else {
                onResult(false)
            }
        }

}