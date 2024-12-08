package com.bimenesvive.bimenesvive.pantallas.autenticacion

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bimenesvive.bimenesvive.autenticacion.EstadoAutenticacion
import com.bimenesvive.bimenesvive.autenticacion.AutenticacionViewModel
import com.bimenesvive.bimenesvive.R
import com.bimenesvive.bimenesvive.ui.theme.MarronInvitado

@Composable
fun PantallaLogin(
    modifier: Modifier = Modifier,
    navController: NavController,
    autenticacionViewModel: AutenticacionViewModel
) {
    val context = LocalContext.current

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    // Dialog para recordar la contraseña
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        RecordarPasswordDialog(
            onDismiss = { showDialog = false },
            onSendEmail = { email ->
                autenticacionViewModel.enviarCorreoRecordarPassword(email, context)
            }
        )
    }

    // Estamos observando el estado de la autenticación para cambiar la UI
    val authenticationState = autenticacionViewModel.estadoAutenticacion.observeAsState()

    LaunchedEffect(authenticationState.value) {
        when (authenticationState.value) {
            is EstadoAutenticacion.Autenticado -> navController.navigate("inicio")
            is EstadoAutenticacion.AutentificacionFallida -> {
                Toast.makeText(context, "Error en la autenticación", Toast.LENGTH_SHORT)
                    .show()
                // Hago un reset del estado a Desautenticado para que el usuario pueda intentar de nuevo
                // y el Toast no salte constantemente
                autenticacionViewModel.reiniciarEstadoAutenticacion()
            }

            else -> Unit
        }
    }

    Box(modifier = modifier.fillMaxSize()) { // Utilizo el box para centrar el contenido
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Registro Image",
                modifier = Modifier.height(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            autenticacionViewModel.login(email, password)
                        } else {
                            Toast.makeText(
                                context,
                                "Por favor, rellena todos los campos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },

                    // El botón estará enabled siempre que no esté autenticando,
                    // así el usuario no puede hacer click varias veces mientras firebase responde
                    enabled = authenticationState.value != EstadoAutenticacion.ProcesandoAutentificacion
                ) {
                    Text(text = "Acceder")
                }

                Button(onClick = { showDialog = true }) {
                    Text("Olvidé mi contraseña")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("registro") }) {
                Text(text = "Todavía no tengo cuenta, quiero registrarme")
            }
        }

        // Box para colocar el TextButton al fondo de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            TextButton(onClick = { navController.navigate("invitado") }) {
                Text(
                    text = "\uD83D\uDC64 Entrar como invitado",
                    color = MarronInvitado
                )
            }
        }
    }
}

/**
 * Dialog para introducir una dirección de email para recordar su contraseña
 */
@Composable
fun RecordarPasswordDialog(onDismiss: () -> Unit, onSendEmail: (String) -> Unit) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Recuperar Contraseña") },
        text = {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSendEmail(email)
                onDismiss()
            }) {
                Text("Enviar")
                // Recibe mail de noreply@bimenes-vive.firebaseapp.com
                // link para cambiar la contraseña
            }
        }
    )
}