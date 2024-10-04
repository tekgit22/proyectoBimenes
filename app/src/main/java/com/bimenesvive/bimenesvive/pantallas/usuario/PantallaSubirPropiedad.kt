package com.bimenesvive.bimenesvive.pantallas.usuario

import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.bimenesvive.bimenesvive.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay


@Composable
fun PantallaSubirPropiedad(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf("Vivienda") }
    val db = FirebaseFirestore.getInstance()

    Scaffold(
        bottomBar = { NavBarInferiorSP(navController as NavHostController, selectedTab) { newTab -> selectedTab = newTab } }
    ) {
        Box(modifier = modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título
                Text(text = "Subir Propiedad")

                Spacer(modifier = Modifier.height(16.dp))

                // Formulario dinámico
                if (selectedTab == "Vivienda") {
                    FormularioVivienda(db, navController)
                } else {
                    FormularioFinca(db, navController)
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}


@Composable
fun FormularioVivienda(db: FirebaseFirestore, navController: NavController) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var habitaciones by remember { mutableIntStateOf(0) }
    var metros by remember { mutableIntStateOf(0) }
    var parroquia by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val userID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val localizacion = GeoPoint(0.0, 0.0) // TO DO
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        TextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        TextField(
            value = habitaciones.toString(),
            onValueChange = { habitaciones = it.toIntOrNull() ?: 0 },
            label = { Text("Habitaciones") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )
        TextField(
            value = metros.toString(),
            onValueChange = { metros = it.toIntOrNull() ?: 0 },
            label = { Text("Metros cuadrados") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )
        TextField(
            value = parroquia,
            onValueChange = { parroquia = it },
            label = { Text("Parroquia") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        TextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val vivienda = hashMapOf(
                "titulo" to titulo,
                "descripcion" to descripcion,
                "habitaciones" to habitaciones,
                "metros" to metros,
                "parroquia" to parroquia,
                "telefono" to telefono,
                "email" to email,
                "userID" to userID,
                "localizacion" to localizacion
            )
            db.collection("viviendas").add(vivienda).addOnSuccessListener { documentReference ->
                val documentID = documentReference.id
                db.collection("viviendas").document(documentID).update("documentID", documentID)
                Toast.makeText(context, "Vivienda subida correctamente", Toast.LENGTH_SHORT).show()
                navController.navigate("mivivienda/$documentID")
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Se produjo un error", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Subir Vivienda")
        }
    }
}


@Composable
fun FormularioFinca(db: FirebaseFirestore, navController: NavController) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var parroquia by remember { mutableStateOf("") }
    var metros by remember { mutableIntStateOf(0) }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var edificable by remember { mutableStateOf(false) }
    val userID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val localizacion = GeoPoint(0.0, 0.0) // Valor invisible al usuario
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        TextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        TextField(
            value = parroquia,
            onValueChange = { parroquia = it },
            label = { Text("Parroquia") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        TextField(
            value = metros.toString(),
            onValueChange = { metros = it.toIntOrNull() ?: 0 },
            label = { Text("Metros cuadrados") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )
        TextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = edificable, onCheckedChange = { edificable = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edificable")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val finca = hashMapOf(
                "titulo" to titulo,
                "descripcion" to descripcion,
                "parroquia" to parroquia,
                "metros" to metros,
                "telefono" to telefono,
                "email" to email,
                "edificable" to edificable,
                "userID" to userID,
                "localizacion" to localizacion
            )
            db.collection("fincas").add(finca).addOnSuccessListener { documentReference ->
                val documentID = documentReference.id
                db.collection("fincas").document(documentID).update("documentID", documentID)
                Toast.makeText(context, "Finca subida correctamente", Toast.LENGTH_SHORT).show()
                navController.navigate("mifinca/$documentID")
            }.addOnFailureListener { e ->
                Toast.makeText(context, "Se produjo un error", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Subir Finca")
        }
    }
}


@Composable
fun NavBarInferiorSP(navController: NavHostController, seccionSeleccionada: String, alSeleccionarSeccion: (String) -> Unit) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
    ) {
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_atras), contentDescription = "Volver") },
            label = { Text("Volver") },
            selected = seccionSeleccionada == "volver",
            onClick = { navController.navigate("inicio") }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_casa), contentDescription = "Vivienda") },
            label = { Text("Vivienda") },
            selected = seccionSeleccionada == "vivienda",
            onClick = { alSeleccionarSeccion("Vivienda") }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_finca), contentDescription = "Finca") },
            label = { Text("Finca") },
            selected = seccionSeleccionada == "finca",
            onClick = { alSeleccionarSeccion("Finca") }
        )
    }
}