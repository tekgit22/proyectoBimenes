package com.bimenesvive.bimenesvive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.bimenesvive.bimenesvive.autenticacion.AutenticacionViewModel
import com.bimenesvive.bimenesvive.navegacion.Navegacion
import com.bimenesvive.bimenesvive.ui.theme.BimenesViveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el modo de pantalla completa en dispositivos con Android 11 o superior.

        // by viewModels() es una extensión de la clase ComponentActivity permite
        // obtener una instancia de un ViewModel asociado a la actividad.
        val autenticacionViewModel: AutenticacionViewModel by viewModels()

        setContent {
            BimenesViveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navegacion(
                        modifier = Modifier.padding(innerPadding),
                        autenticacionViewModel = autenticacionViewModel
                    )
                // El Scaffold calcula un innerPadding que representa el espacio necesario
                // para que el contenido no se superponga con otros elementos de la interfaz de usuario
                // (como la barra de navegación o la barra de estado).
                }
            }
        }
    }
}


// Alternativa sin pasar innerPadding ni el modifier a Navegacion:

//setContent {
//    BimenesViveTheme {
//        Scaffold(modifier = Modifier.fillMaxSize()) {
//            Navegacion(autenticacionViewModel = autenticacionViewModel)
//        }
//    }
//}

