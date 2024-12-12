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
        enableEdgeToEdge()
        val autenticacionViewModel: AutenticacionViewModel by viewModels()
        setContent {
            BimenesViveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navegacion(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        autenticacionViewModel = autenticacionViewModel
                    )
                }
            }
        }
    }
}

