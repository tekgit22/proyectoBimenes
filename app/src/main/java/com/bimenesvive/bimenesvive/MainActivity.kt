package com.bimenesvive.bimenesvive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bimenesvive.bimenesvive.ui.theme.BimenesViveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ESTUDIAR
        val authenticationViewModel : AuthenticationViewModel by viewModels()

        setContent {
            BimenesViveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navegacion(modifier = Modifier.padding(innerPadding), authenticationViewModel = authenticationViewModel)
                    //TENGO QUE MIRAR POR QUE METE PADDING AHÍ
                    //desde aquí vamos a la navegación



                }
            }
        }
    }
}

