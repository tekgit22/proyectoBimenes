package com.bimenesvive.bimenesvive.pantallas.invitado

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PantallaInvitado(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { /* TO DO */ }, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
        ) {
            Text(text = "Ver Inmuebles", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(15.dp))
        Button(onClick = { /* TO DO */ }, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
        ) {
            Text(text = "Conoce Bimenes", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
        TextButton(onClick = { navController.navigate("registro") }) {
            Text(text = "Quiero registrarme para ver más", fontSize = 18.sp)
        }
    }
}