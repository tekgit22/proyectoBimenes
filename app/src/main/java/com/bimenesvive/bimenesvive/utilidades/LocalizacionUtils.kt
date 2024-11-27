package com.bimenesvive.bimenesvive.utilidades

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bimenesvive.bimenesvive.modelos.Finca
import com.bimenesvive.bimenesvive.modelos.Vivienda
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

const val LOCATION_PERMISSION_REQUEST_CODE = 1000  // Código de solicitud de permiso de ubicación
// Este código se utiliza para identificar la solicitud de permiso de ubicación en el metodo onRequestPermissionsResult


/**
 * Comprueba si la aplicación tiene permiso para acceder a la ubicación del dispositivo.
 * Si no tiene permiso, solicita permiso al usuario.
 * @param activity Actividad desde la que se solicita el permiso.
 * @return True si la aplicación tiene permiso de ubicación, false en caso contrario.
 */
fun comprobarPermisosLocalizacion(activity: Activity): Boolean {
    return if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE) // Solicitar permiso de ubicación
        false
    } else {
        true
    }
}

/**
 * Obtiene la ubicación actual del dispositivo.
 * @param activity Actividad desde la que se solicita la ubicación.
 * @param onLocationReceived Función que se ejecuta cuando se recibe la ubicación.
 */
fun obtenerUbicacionActual(activity: Activity, onLocationReceived: (Location) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    val locationRequest = LocationRequest.Builder(10000)
        .setMinUpdateIntervalMillis(5000)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .build()

    if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        return
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                onLocationReceived(location) // Ejecuta la función que se pasa como parámetro para guardar la ubicación
                fusedLocationClient.removeLocationUpdates(this) // Deja de recibir actualizaciones de ubicación
            }
        }
    }, android.os.Looper.getMainLooper()) // Ejecuta el callback en el hilo principal
}

/**
 * Guarda la ubicación de una vivienda en Firestore.
 * @param vivienda Vivienda a la que se le va a guardar la ubicación.
 * @param context Actividad desde la que se solicita la ubicación.
 * @param documentID ID del documento de la vivienda en Firestore.
 * @param onLocationSaved Función que se ejecuta cuando se guarda la ubicación.
 */
fun guardarLocalizacionVivienda(vivienda: Vivienda, context: Activity, documentID: String, onLocationSaved: (Location) -> Unit) {
    if (comprobarPermisosLocalizacion(context)) {
        obtenerUbicacionActual(context) { location ->
            val db = FirebaseFirestore.getInstance()
            val viviendaRef = db.collection("viviendas").document(documentID)
            val geoPoint = GeoPoint(location.latitude, location.longitude)

            Log.d("PantallaVivienda", "Guardando localización: lat=${location.latitude}, lon=${location.longitude}")
            //Toast.makeText(context, "lat=${location.latitude}, lon=${location.longitude}", Toast.LENGTH_SHORT).show()
            Toast.makeText(context, "Localización guardada correctamente", Toast.LENGTH_SHORT).show()

            val data = mapOf("localizacion" to geoPoint)
            viviendaRef.update(data)
                .addOnSuccessListener {
                    Log.d("PantallaVivienda", "Localización guardada correctamente")
                    onLocationSaved(location)
                }
                .addOnFailureListener { e ->
                    Log.e("PantallaVivienda", "Error al guardar la localización en Firestore", e)
                    Toast.makeText(context, "Error al guardar la localización", Toast.LENGTH_SHORT).show()
                }
        }
    } else {
        Log.d("PantallaVivienda", "Permiso de ubicación no concedido")
    }
}


/**
 * Guarda la ubicación de una finca en Firestore.
 * @param finca Finca a la que se le va a guardar la ubicación.
 * @param context Actividad desde la que se solicita la ubicación.
 * @param documentID ID del documento de la finca en Firestore.
 * @param onLocationSaved Función que se ejecuta cuando se guarda la ubicación.
 */
fun guardarLocalizacionFinca(finca: Finca, context: Activity, documentID: String, onLocationSaved: (Location) -> Unit) {
    if (comprobarPermisosLocalizacion(context)) {
        obtenerUbicacionActual(context) { location ->
            val db = FirebaseFirestore.getInstance()
            val fincaRef = db.collection("fincas").document(documentID)
            val geoPoint = GeoPoint(location.latitude, location.longitude)

            Log.d("PantallaFinca", "Guardando localización: lat=${location.latitude}, lon=${location.longitude}")
            Toast.makeText(context, "lat=${location.latitude}, lon=${location.longitude}", Toast.LENGTH_SHORT).show()

            val data = mapOf("localizacion" to geoPoint)
            fincaRef.update(data)
                .addOnSuccessListener {
                    Log.d("PantallaFinca", "Localización guardada exitosamente en Firestore")
                    Toast.makeText(context, "Localización guardada exitosamente", Toast.LENGTH_SHORT).show()
                    onLocationSaved(location)
                }
                .addOnFailureListener { e ->
                    Log.e("PantallaFinca", "Error al guardar la localización en Firestore", e)
                    Toast.makeText(context, "Error al guardar la localización", Toast.LENGTH_SHORT).show()
                }
        }
    } else {
        Log.d("PantallaFinca", "Permiso de ubicación no concedido")
    }
}

/**
 * Comprueba si el GPS está habilitado en el dispositivo.
 * @param context Contexto de la aplicación.
 * @return True si el GPS está habilitado, false en caso contrario.
 */
fun isGPSEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

/**
 * Comprueba si el dispositivo tiene GPS.
 * @param context Contexto de la aplicación.
 * @return True si el dispositivo tiene GPS, false en caso contrario.
 */
fun tieneGPS(context: Context): Boolean {
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
}