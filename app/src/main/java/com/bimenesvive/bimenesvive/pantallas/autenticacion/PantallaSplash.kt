package com.bimenesvive.bimenesvive.pantallas.autenticacion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.bimenesvive.bimenesvive.autenticacion.EstadoAutenticacion
import com.bimenesvive.bimenesvive.autenticacion.AutenticacionViewModel
import com.bimenesvive.bimenesvive.R
import kotlinx.coroutines.delay

@Composable
fun PantallaSplash(navController: NavController, autenticacionViewModel: AutenticacionViewModel) {
    LaunchedEffect(Unit) {
        delay(2000) // Espera 2 segundos mostrando el logo y después navega a la pantalla de inicio o de login
        val pantallaDestino =
            if (autenticacionViewModel.estadoAutenticacion.value is EstadoAutenticacion.Autenticado) {
                "inicio"
            } else {
                "login"
            }
        navController.navigate(pantallaDestino) {
            popUpTo("splash") { inclusive = true }
            //El metodo popUpTo() elimina todas las pantallas del backstack hasta la pantalla especificada
            //inclusive = true significa que también se elimina la pantalla especificada
            //La pila de navegación no tendrá esta pantalla y el usuario no puede retroceder a ella
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo")
    }
}