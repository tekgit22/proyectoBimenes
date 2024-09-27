package com.bimenesvive.bimenesvive

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bimenesvive.bimenesvive.pantallas.usuario.PantallaPrincipal
import com.bimenesvive.bimenesvive.pantallas.invitado.PantallaInvitado
import com.bimenesvive.bimenesvive.pantallas.autenticacion.PantallaLogin
import com.bimenesvive.bimenesvive.pantallas.autenticacion.PantallaRegistro
import com.bimenesvive.bimenesvive.pantallas.autenticacion.PantallaSplash


@Composable
fun Navegacion(modifier: Modifier = Modifier, authenticationViewModel: AuthenticationViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash", builder = {
        composable("splash") {
            PantallaSplash(navController, authenticationViewModel)
        }
        composable("login") {
            PantallaLogin(modifier, navController, authenticationViewModel)
        }
        composable("registro") {
            PantallaRegistro(modifier, navController, authenticationViewModel)
        }
        composable("inicio") {
            PantallaPrincipal(modifier, navController, authenticationViewModel)
        }
        composable("invitado") {
            PantallaInvitado(modifier, navController)
        }
    })
}