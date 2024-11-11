package com.bimenesvive.bimenesvive.componentes

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.CircleCropTransformation
import com.bimenesvive.bimenesvive.R


/**
 * Muestra una imagen con un placeholder mientras se carga la imagen del servidor
 * @param uri
 * @param modifier
 */
@Composable
fun ImagenConPlaceholder(uri: Uri, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(uri)
            .size(Size.ORIGINAL)
            .placeholder(R.drawable.loading_spinner)
            .crossfade(true)
            .transformations(CircleCropTransformation())
            .build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
    )
}