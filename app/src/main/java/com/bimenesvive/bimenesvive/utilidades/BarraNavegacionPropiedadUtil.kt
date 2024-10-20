package com.bimenesvive.bimenesvive.utilidades

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bimenesvive.bimenesvive.R

@Composable
fun NavBarViendoPropiedad(navController: NavHostController) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
    ) {
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_atras), contentDescription = "Explorar") },
            label = { Text("Explorar") },
            selected = false, //No necesito seleccionar nada, va a navegar a otra pantalla
            //onClick = { navController.navigate("explorar") }
            onClick = {
                navController.navigate("explorar") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_casa), contentDescription = "Menú") },
            label = { Text("Menú") },
            selected = false, //No necesito seleccionar nada, va a navegar a otra pantalla
            //onClick = { navController.navigate("inicio") }
            onClick = {
                navController.navigate("inicio") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        )
    }
}

@Composable
fun NavBarVolverInicio(navController: NavHostController) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
    ) {
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_casa), contentDescription = "Menú") },
            label = { Text("Menú") },
            selected = false, //No necesito seleccionar nada, va a navegar a otra pantalla
//            onClick = { navController.navigate("inicio") }
            onClick = {
                navController.popBackStack() //Vuelve a la pantalla anterior y elimina la actual
            }
        )
    }
}