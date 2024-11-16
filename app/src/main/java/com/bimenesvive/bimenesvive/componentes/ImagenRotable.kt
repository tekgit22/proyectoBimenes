package com.bimenesvive.bimenesvive.componentes

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

/**
 * Muestra la imagen rotada si es horizontal (horizontal = ancho > alto)
 * @param imageUri URI de la imagen
 * @param modifier Modificador de la imagen
 */
@Composable
fun ImagenRotable(imageUri: Uri, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(model = imageUri)

    var rotacion by remember { mutableStateOf(0f) }
    var contentScale by remember { mutableStateOf(ContentScale.Fit) }
    // ContentScale.Fit se ajusta a la imagen sin deformarla

    LaunchedEffect(painter.state) {
        when (painter.state) {
            is AsyncImagePainter.State.Success -> {
                val imageSize =
                    (painter.state as AsyncImagePainter.State.Success).painter.intrinsicSize
                // Determina el tamaño original de la imagen
                if (imageSize.width > imageSize.height) {
                    rotacion = 90f
                    contentScale = ContentScale.FillWidth
                    // Llena el ancho del contenedor, ajustando la altura en consecuencia sin deformar la imagen (mantiene el aspect ratio)
                } else {
                    contentScale = ContentScale.FillHeight
                    // Llena la altura del contenedor, ajustando el ancho en consecuencia sin deformar la imagen (mantiene el aspect ratio)
                }
            }

            else -> {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = contentScale,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    rotationZ = rotacion,
                    scaleX = if (rotacion != 0f) 1.5f else 1f,
                    scaleY = if (rotacion != 0f) 1.5f else 1f
                    // El escalado 1f es el tamaño original, 1.5f es un tamaño más grande
                )
        )
    }
}

// Flujo del componente ImagenRotable:
// Las variables de estado manejarán la rotación y la escala de la imagen
// LaunchedEffect reaccionará a los cambios en el estado del `painter` (estará esperando a que la imagen se cargue)
// Obtiene el tamaño de la imagen
// Si la imagen es más ancha que alta, se rota 90 grados y ajusta la escala para llenar el ancho del contenedor
// Si la imagen es más alta que ancha, ajusta la escala para llenar la altura del contenedor
// La UI del componente es un `Box` que centra su contenido
// Dentro de ese Box se muestra la imagen, que estará rotada y escalada según las variables de estado