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
import com.bimenesvive.bimenesvive.pantallas.usuario.PantallaAnuncio
import com.bimenesvive.bimenesvive.pantallas.usuario.PantallaBimenes
import com.bimenesvive.bimenesvive.pantallas.usuario.PantallaExplorar
import com.bimenesvive.bimenesvive.pantallas.usuario.PantallaFinca
import com.bimenesvive.bimenesvive.pantallas.usuario.PantallaMisPropiedades
import com.bimenesvive.bimenesvive.pantallas.usuario.PantallaSubirPropiedad
import com.bimenesvive.bimenesvive.pantallas.usuario.PantallaTransados
import com.bimenesvive.bimenesvive.pantallas.usuario.PantallaVivienda


@Composable
fun Navegacion(modifier: Modifier = Modifier, autenticacionViewModel: AutenticacionViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash", builder = {
        composable("splash") {
            PantallaSplash(navController, autenticacionViewModel)
        }
        composable("login") {
            PantallaLogin(modifier, navController, autenticacionViewModel)
        }
        composable("registro") {
            PantallaRegistro(modifier, navController, autenticacionViewModel)
        }
        composable("inicio") {
            PantallaPrincipal(modifier, navController, autenticacionViewModel)
        }
        composable("invitado") {
            PantallaInvitado(modifier, navController)
        }
        composable("explorar") {
            PantallaExplorar(modifier, navController)
        }
        composable("vivienda/{documentID}") { entry ->
            val documentID = entry.arguments?.getString("documentID") ?: ""
            PantallaVivienda(modifier, navController, documentID)
        }
        composable("finca/{documentID}") { entry ->
            val documentID = entry.arguments?.getString("documentID") ?: ""
            PantallaFinca(modifier, navController, documentID)
        }
        composable("subirPropiedad") {
            PantallaSubirPropiedad(modifier, navController)
        }
        composable("bimenes") {
            PantallaBimenes(modifier, navController)
        }
        composable("anuncio") {
            PantallaAnuncio(modifier, navController)
        }
        composable("mispropiedades") {
            PantallaMisPropiedades(modifier, navController)
        }
        composable("transados") {
            PantallaTransados(modifier, navController)
        }

    })
}