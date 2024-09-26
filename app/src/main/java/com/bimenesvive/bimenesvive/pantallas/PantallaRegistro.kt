package com.bimenesvive.bimenesvive.pantallas

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bimenesvive.bimenesvive.AuthenticationState
import com.bimenesvive.bimenesvive.AuthenticationViewModel

@Composable
fun PantallaRegistro(
    modifier: Modifier = Modifier,
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel
) {

    val context = LocalContext.current

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    //Estamos observando el estado de la autenticación para cambiar la UI
    val authenticationState = authenticationViewModel.authenticationState.observeAsState()

    LaunchedEffect(authenticationState.value) {  //estudiar LaunchedEffect
        when (authenticationState.value) {
            is AuthenticationState.Autenticado -> navController.navigate("inicio")
            else -> Unit
        }
        //When the return type of a function is Unit it means the function does not return anything

    }

    Column(
        //MODIFICAR UI
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "REGISTRO", fontSize = 35.sp)
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(value = email, onValueChange = {
            email = it
        },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(value = password, onValueChange = {
            password = it
        },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                // Si el password o el email están vacíos, no permito el registro
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    authenticationViewModel.registro(email, password)
                } else {
                    Toast.makeText(context, "Email y password no pueden estar vacíos", Toast.LENGTH_SHORT).show()
                }
            },
            //El botón estará enabled siempre que no esté autenticando, así el usuario no puede hacer click varias veces mientras firebase responde
            enabled = authenticationState.value != AuthenticationState.ProcesandoAutentificacion
        ) {
            Text(text = "Crear Cuenta") //ARCHIVO DE TEXTO
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text(text = "Acceder a mi cuenta") //ARCHIVO DE TEXTO
        }


    }
}