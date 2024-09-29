package com.bimenesvive.bimenesvive.modelo

import com.google.firebase.firestore.GeoPoint

data class Finca(
    val descripcion: String = "",
    val localizacion: GeoPoint? = null,
    val metros: Int = 0,
    val parroquia: String = "",
    val titulo: String = "",
    val userID: String = "",
    val documentID: String = "",
    val telefono: String = "",
    val email: String = "",
    val edificable: Boolean = false
)