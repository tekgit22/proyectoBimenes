package com.bimenesvive.bimenesvive.pantallas.comunes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bimenesvive.bimenesvive.R
import com.bimenesvive.bimenesvive.componentes.NavBarVolverInicio

@Composable
fun PantallaBimenes(modifier: Modifier, navController: NavHostController) {
    val scrollState = rememberScrollState() // Almacena el estado de scroll

    // Column con rememberScrollState() es eficiente para listas pequeñas o contenido fijo (esta pantalla es fija)
    // Lazycolumn es más eficiente para listas grandes o contenido dinámico que se recicla

    Scaffold(
        bottomBar = { NavBarVolverInicio(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState) // Este es el modificador que permite hacer scroll a una Column
        ) {
            Text(
                text = "CONOCE BIMENES",
                fontSize = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.bimenes1),
                contentDescription = "Foto Naturaleza Bimenes",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                // """ es un string literal de kotlin que permite escribir texto en varias líneas
                text = """
                    Los más antiguos vestigios arqueológicos de Bimenes, corresponden a etapas posteriores a la neolitización, probablemente en relación con los primeros ciclos metalúrgicos. De ellos se inventariaron tres túmulos: uno en Piedrafita, en la falda occidental de Peñamayor; otro en Lancosa, próximo a la divisoria de Nava; y un tercero en Campabaxera, cerca del límite con Siero.

                    El nombre primitivo de Bimenes está escrito con V, Vimenes, del latín vinies, lo cual puede esclarecer el carácter de paso de estas tierras para las tribus nómadas, que huyendo de la sequía y escasez de otras regiones, llegaron con sus ganados hasta las montañas de Peña Mayor y decidieron quedarse ante la magnificencia del paraje.

                    Vimenes permaneció largo tiempo en el lenguaje escrito; solamente a principios de siglo comenzo a escribirse definitivamente con B.

                    En documentos antiguos, el personaje histórico legendario Suero de Bimenes aparece con los nombres de Viniens y Vinieres. Tal figura fue quién dió nombre a la actual capital del concejo, Martimporra, nombre que perteneció al hijo del noble Menén Porra, con quien Suero dirimió un combate, por causa de la hermana de áquel, Elvira Porra, en las inmediaciones de la actual capital. Suero sale victorioso, naciendo una amistad con su rival. Desde entonces el campo de la justa se denominó Martimporra.

                    Primitivamente, tres cotos constituían el término. La Riera o Ribera en San Julián; Melendreros en San Emeterio, y el señorial de Taballes, también en San Emeterio.

                    Con fecha 18 de julio de 1.161 existe un documento por el que Didaco Petri da a su mujer Gloria Fernandi, entre otros bienes, la heredad que posee en Uimenes.

                    El primer tercio del siglo XIX reportará notables transformaciones. Los cotos de Melendreros y Taballes se emancipan y se integran, con el coto de la Riera, en el concejo de Bimenes, concebido como un ente autónomo con administración única.

                    Las actividades mineras alcanzaron el término municipal de Bimenes, pero se restringieron a yacimientos superficiales, explotados mediante pequeñas galerías abiertas en las laderas de las montañas.

                    A finales del siglo XIX surgieron proyectos para el establecimiento de un ferrocarril de vía estrecha, las obras se llegaron a iniciar pero nunca se llegó a poner en funcionamiento la línea férrea.

                    En la Guerra Civil, el concejo quedó enclavado en la zona republicana y en esta situación se mantuvo hasta que en octubre de 1937 cayeron los últimos reductos del Frente Norte. Sin embargo, una vez decidido el conflicto bélico en Asturias, algunas partidas de guerrilleros fugados, siguieron actuando en la comarca, ocultos en los montes de Peña Mayor.

                    Tras la postguerra, el concejo evolucionó ligado al sector minero asturiano, con la particularidad de que en el municipio hay mineros pero no minas.
                """.trimIndent(), // trimIndent() elimina los espacios en blanco al inicio de cada línea
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.bimenes2),
                contentDescription = "Foto Molino",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}
