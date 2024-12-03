package com.bimenesvive.bimenesvive.componentes

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment


/**
 * Muestra el número de caracteres actuales y el máximo permitido para un campo de texto.
 * @param currentLength Número de caracteres actuales
 * @param maxLength Número máximo de caracteres permitidos
 */
@Composable
fun TextLimiteTitulo(currentLength: Int, maxLength: Int) {
    //Necesito contenerlo en un box para poder alinear el texto a la derecha
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = "$currentLength / $maxLength",
            style = MaterialTheme.typography.bodySmall,
            color = if (currentLength == maxLength) Color.Red else Color.Gray
        )
    }
}