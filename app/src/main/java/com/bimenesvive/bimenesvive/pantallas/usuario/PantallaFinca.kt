package com.bimenesvive.bimenesvive.pantallas.usuario

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun PantallaFinca(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    documentID: String
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Pantalla Finca con ID: $documentID")
    }
}