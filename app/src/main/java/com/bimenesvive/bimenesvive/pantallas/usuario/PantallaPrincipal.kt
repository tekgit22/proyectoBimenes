package com.bimenesvive.bimenesvive.pantallas.usuario

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bimenesvive.bimenesvive.AuthenticationState
import com.bimenesvive.bimenesvive.AuthenticationViewModel

@Composable
fun PantallaPrincipal(
    modifier: Modifier = Modifier,
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel
) {

    //Estamos observando el estado de la autenticación para cambiar la UI
    val authenticationState = authenticationViewModel.authenticationState.observeAsState()

    LaunchedEffect(authenticationState.value) {  //estudiar LaunchedEffect
        when (authenticationState.value) {
            is AuthenticationState.Desautenticado -> navController.navigate("login")
            else -> Unit
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Título
        Box(modifier = Modifier.padding(8.dp).align(Alignment.TopCenter)) {
            Text(text = "Bimenes Vive", fontSize = 35.sp)
        }

        // Botones centrados
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = { navController.navigate("explorar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "\uD83C\uDFE0") // Emoji de casa
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Ver Inmuebles")
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
                    Text(text = "\uD83D\uDCE5") // Emoji de nube con flecha hacia arriba
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
                    Text(text = "\u2B50") // Emoji de estrella
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Favoritos")
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
                    Text(text = "\uD83D\uDCC4") // Emoji de hojas de folio
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Mis Propiedades")
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
                    Text(text = "\uD83C\uDF32") // Emoji de árbol
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Conoce Bimenes")
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