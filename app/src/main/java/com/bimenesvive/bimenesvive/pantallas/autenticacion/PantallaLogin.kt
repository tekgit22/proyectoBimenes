package com.bimenesvive.bimenesvive.pantallas.autenticacion

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bimenesvive.bimenesvive.AuthenticationState
import com.bimenesvive.bimenesvive.AuthenticationViewModel
import com.bimenesvive.bimenesvive.R

@Composable
fun PantallaLogin(
    modifier: Modifier = Modifier,
    navController: NavController,
    authenticationViewModel: AuthenticationViewModel
) {
    val context = LocalContext.current
    //recurda que con null sin localcontext la app crashea

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    //Estamos observando el estado de la autenticación para cambiar la UI
    val authenticationState = authenticationViewModel.authenticationState.observeAsState()

    LaunchedEffect(authenticationState.value) {
        when (authenticationState.value) {
            is AuthenticationState.Autenticado -> navController.navigate("inicio")
            is AuthenticationState.AutentificacionFallida -> {
                Toast.makeText(context, "Error en la autenticación", Toast.LENGTH_SHORT)
                    .show() //podría usar el mensaje de error que viene en el estado
                //y en vez de un toast, mejor usar un snackbar ?
                //Tengo que mirar cómo diferenciar un error al autenticarse o que pass/email son incorrectos

                //Hago un reset del estado para que el Toast no salte sin parar si navegamos por la UI
                authenticationViewModel.reiniciarEstadoAutenticacion()
            }

            else -> Unit
        }

    }

    Box(modifier = modifier.fillMaxSize()) { //Utilizo el box para centrar el contenido
        Column(
            //MODIFICAR UI
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Registro Image",
                modifier = Modifier.height(100.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))


            Text(text = "INICIAR SESION", fontSize = 35.sp)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = email, onValueChange = {
                    email = it
                },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = password, onValueChange = {
                    password = it
                },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    // Si el password o el email están vacíos, no permito el login
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        authenticationViewModel.login(email, password)
                    } else {
                        Toast.makeText(
                            context,
                            "Por favor, rellena todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                //El botón estará enabled siempre que no esté autenticando, así el usuario no puede hacer click varias veces mientras firebase responde
                enabled = authenticationState.value != AuthenticationState.ProcesandoAutentificacion
            ) {
                Text(text = "Acceder") //ARCHIVO DE TEXTO
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = { navController.navigate("registro") }) {
                Text(text = "Todavía no tengo cuenta, quiero registrarme") //ARCHIVO DE TEXTO
            }
        }

        Box( //Este box está para alojar el TextButton en el fondo de la pantalla
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            TextButton(onClick = { navController.navigate("invitado") }) {
                Text(text = "\uD83D\uDC64 Entrar como invitado",
                color = Color(0xFF8B4513)
                )
            }
        }


    }
}