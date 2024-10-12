package com.bimenesvive.bimenesvive.utilidades

import android.app.Activity
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bimenesvive.bimenesvive.modelo.Finca
import com.bimenesvive.bimenesvive.modelo.Vivienda
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

const val LOCATION_PERMISSION_REQUEST_CODE = 1000

fun checkLocationPermission(activity: Activity): Boolean {
    return if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        false
    } else {
        true
    }
}

fun getCurrentLocation(activity: Activity, onLocationReceived: (Location) -> Unit) {
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
                onLocationReceived(location)
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }, android.os.Looper.getMainLooper())
}

fun saveViviendaLocation(vivienda: Vivienda, context: Activity, documentID: String, onLocationSaved: (Location) -> Unit) {
    if (checkLocationPermission(context)) {
        getCurrentLocation(context) { location ->
            val db = FirebaseFirestore.getInstance()
            val viviendaRef = db.collection("viviendas").document(documentID)
            val geoPoint = GeoPoint(location.latitude, location.longitude)

            Log.d("PantallaVivienda", "Guardando localización: lat=${location.latitude}, lon=${location.longitude}")
            Toast.makeText(context, "lat=${location.latitude}, lon=${location.longitude}", Toast.LENGTH_SHORT).show()

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

fun saveFincaLocation(finca: Finca, context: Activity, documentID: String, onLocationSaved: (Location) -> Unit) {
    if (checkLocationPermission(context)) {
        getCurrentLocation(context) { location ->
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