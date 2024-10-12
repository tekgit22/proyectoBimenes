package com.bimenesvive.bimenesvive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.bimenesvive.bimenesvive.ui.theme.BimenesViveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // by viewModels() es una extensión de la clase ComponentActivity permite
        // obtener una instancia de un ViewModel asociado a la actividad.
        val authenticationViewModel : AuthenticationViewModel by viewModels()

        setContent {
            BimenesViveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navegacion(modifier = Modifier.padding(innerPadding), authenticationViewModel = authenticationViewModel)
                    //el modificador de innerPadding está para asegurarme de que la navegación
                    //se pueda superponer con otros elementos de la UI como una barra de navegación
                }
            }
        }
    }
}

