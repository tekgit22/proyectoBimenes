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
import androidx.compose.material3.Icon


@Composable
fun PantallaPrincipal(
    modifier: Modifier = Modifier,
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel
) {
    val authenticationState = authenticationViewModel.authenticationState.observeAsState()

    LaunchedEffect(authenticationState.value) {
        when (authenticationState.value) {
            is AuthenticationState.Desautenticado -> navController.navigate("login")
            else -> Unit
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Título
        Box(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopCenter)
        ) {
            Image(painter = painterResource(id = R.drawable.logotest), contentDescription = "Logo")
        }

        // Botones centrados
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                onClick = { /* TO DO */ },
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
                onClick = { /* TO DO */ },
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
        }
    }

    // Botón de logoff en la parte inferior
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        TextButton(onClick = {
            authenticationViewModel.cerrarSesion()
            navController.navigate("login")
        }) {
            Text(text = "Cerrar sesión")
        }
    }
}