package com.bimenesvive.bimenesvive.pantallas.invitado

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bimenesvive.bimenesvive.R


@Composable
fun PantallaInvitado(
    modifier: Modifier = Modifier,
    navController: NavController,
) {

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
                onClick = {  },
                enabled = false,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
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
                onClick = {  },
                enabled = false,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
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
                onClick = { navController.navigate("bimenes")},
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
                onClick = { /* TO DO , recordar que no puede escribir anuncios */ },
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



    // Botón para volver al registro en la parte inferior
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        TextButton(onClick = { navController.navigate("registro") }) {
            Text(text = "Quiero registrarme para ver más", fontSize = 18.sp)
        }
    }
}