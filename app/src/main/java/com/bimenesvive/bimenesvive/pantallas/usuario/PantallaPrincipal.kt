package com.bimenesvive.bimenesvive.pantallas.usuario

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bimenesvive.bimenesvive.AuthenticationState
import com.bimenesvive.bimenesvive.AuthenticationViewModel
import com.bimenesvive.bimenesvive.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import com.google.firebase.auth.FirebaseAuth


@Composable
fun PantallaPrincipal(
    modifier: Modifier = Modifier,
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel
) {
    val authenticationState = authenticationViewModel.authenticationState.observeAsState()
    val userID = FirebaseAuth.getInstance().currentUser?.uid
    val adminID = "4wOzuh8c8GhYeP3qbgn6Ja888F02"

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

        if (userID == adminID) {
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
                authenticationViewModel.cerrarSesion()
                navController.navigate("login")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Cerrar sesión")
        }
    }
}